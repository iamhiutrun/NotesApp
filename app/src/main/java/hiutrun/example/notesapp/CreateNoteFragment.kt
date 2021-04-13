package hiutrun.example.notesapp

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hiutrun.example.notesapp.database.NotesDatabase
import hiutrun.example.notesapp.entities.Notes
import hiutrun.example.notesapp.util.NoteBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.android.synthetic.main.fragment_create_note.imgMore
import kotlinx.android.synthetic.main.fragment_create_note.layoutImage
import kotlinx.android.synthetic.main.fragment_create_note.layoutWebUrl
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_notes_bottom_sheet.*
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import java.util.regex.Pattern

class CreateNoteFragment : BaseFragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    var selectedColor = "#171C26"
    var currentDate : String? = null
    private var READ_STORAGE_PERM  = 123
    private var REQUEST_CODE_IMAGE  = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = requireArguments().getInt("noteId",-1)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(noteId!= -1){

            launch {
                context?.let {
                    var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                    colorView.setBackgroundColor(Color.parseColor(notes.color))
                    etNoteDesc.setText(notes.noteText)
                    etNoteSubTitle.setText(notes.subTile)
                    etNoteTitle.setText(notes.title)

                    if(notes.img !=""){
                        selectedImagePath = notes.img!!
                        imgNote.setImageBitmap(BitmapFactory.decodeFile(notes.img))
                        layoutImage.visibility = View.VISIBLE
                        imgNote.visibility = View.VISIBLE
                        imgDelete.visibility = View.VISIBLE
                    }else{
                        imgNote.visibility = View.GONE
                        imgDelete.visibility = View.GONE
                        layoutImage.visibility = View.GONE

                    }

                    if(notes.webLink !=""){
                        webLink = notes.webLink.toString()
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        imgUrlDelete.visibility = View.VISIBLE
                        etWebLink.setText(notes.webLink)
                    }else{
                        imgUrlDelete.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }

                }
            }

        }


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                broadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentDate = sdf.format(Date())

        tvDateTime.text = currentDate
        colorView.setBackgroundColor(Color.parseColor(selectedColor))

        imgDone.setOnClickListener{

            if(noteId!=-1){
                updateNote()
            }else{
                saveNote()
            }
        }

        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        imgMore.setOnClickListener {
            var noteBottomSheetFragment = NoteBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(requireActivity().supportFragmentManager,"Note Bottom Sheet Fragment")
        }

        btnOk.setOnClickListener {
            if(!etWebLink.text.toString().trim().isNullOrEmpty()){
                checkWebUrl()
            }else{
                Toast.makeText(requireContext(),"Url is Required", Toast.LENGTH_SHORT).show()
            }

            tvWebLink.setOnClickListener{
                var intent = Intent(Intent.ACTION_VIEW,Uri.parse(etWebLink.text.toString()))
                startActivity(intent)
            }
        }

        btnCancel.setOnClickListener {
            if(noteId!=-1){
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            }else{
                layoutWebUrl.visibility = View.GONE
            }
            layoutWebUrl.visibility = View.GONE
        }

        imgDelete.setOnClickListener {
            layoutImage.visibility = View.GONE
            selectedImagePath = ""
        }

        imgUrlDelete.setOnClickListener {
            webLink = ""
            imgUrlDelete.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }
    }


    private fun updateNote(){
        launch {

            context?.let {
                val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                notes.title = etNoteTitle.text.toString()
                notes.subTile = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.img = selectedImagePath
                notes.webLink = webLink


                NotesDatabase.getDatabase(it).noteDao().updateNotes(notes)
                etNoteDesc.setText("")
                etNoteSubTitle.setText("")
                etNoteTitle.setText("")
                imgNote.visibility = View.GONE
                layoutImage.visibility = View.GONE
                tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }

        }
    }
    private fun saveNote(){
        if(etNoteTitle.text.isNullOrEmpty()){
            Toast.makeText(context,"Note Title is Required", Toast.LENGTH_SHORT).show()
        }
        else if(etNoteSubTitle.text.isNullOrEmpty()){
            Toast.makeText(context,"Note Sub Title is Required", Toast.LENGTH_SHORT).show()
        }
        else if(etNoteDesc.text.isNullOrEmpty()){
            Toast.makeText(context,"Note Description is Required", Toast.LENGTH_SHORT).show()
        }else{

            launch {
                val notes = Notes()
                notes.title = etNoteTitle.text.toString()
                notes.subTile = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.img = selectedImagePath
                notes.webLink = webLink

                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    etNoteDesc.setText("")
                    etNoteSubTitle.setText("")
                    etNoteTitle.setText("")
                    imgNote.visibility = View.GONE
                    layoutImage.visibility = View.GONE
                    tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }

            }

        }
    }

    private fun deleteNote(){
        launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun checkWebUrl(){
        if(Patterns.WEB_URL.matcher(etWebLink.text.toString()).matches()){
            layoutWebUrl.visibility = View.GONE
            etWebLink.isEnabled = false
            webLink = etWebLink.text.toString()
            tvWebLink.visibility = View.VISIBLE
            tvWebLink.text = webLink
        }else{
            Toast.makeText(requireContext(),"Url is not valid", Toast.LENGTH_SHORT).show()
        }
    }

    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var actionColor = intent!!.getStringExtra("action")
            when(actionColor!!){

                "Blue" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Yellow" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "White" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Purple" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Green" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Orange" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Black" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Image" ->{
                    readStorageTask()
                    layoutWebUrl.visibility = View.GONE
                }

                "WebUrl" ->{
                    // show web url layout
                    layoutWebUrl.visibility = View.VISIBLE
                }

                "DeleteNote" ->{
                    // delete note
                    deleteNote()
                }

                else ->{
                    imgNote.visibility = View.GONE
                    layoutImage.visibility = View.GONE
                    layoutWebUrl.visibility = View.GONE
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }

            }
        }

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
                broadcastReceiver
        )

        super.onDestroy()
    }


    private fun hasReadStoragePerm():Boolean{
        return EasyPermissions.hasPermissions(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun hasWriteStoragePerm():Boolean{
        return EasyPermissions.hasPermissions(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun readStorageTask(){
        if (hasReadStoragePerm()){

            pickImageFromGallery()
        }else{
            EasyPermissions.requestPermissions(
                    requireActivity(),
                    getString(R.string.storage_permission_text),
                    READ_STORAGE_PERM,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery(){
        var intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(requireActivity().packageManager)!=null){
            startActivityForResult(intent,REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri:Uri): String? {
        var filePath:String?=null
        var cursor = requireActivity().contentResolver.query(contentUri,null,null,null,null)
        if(cursor==null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CODE_IMAGE && resultCode == RESULT_OK){
            if(data!=null){
                var selectedImageUrl = data.data
                if(selectedImageUrl!=null){
                    try {
                        var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        imgNote.setImageBitmap(bitmap)
                        imgNote.visibility = View.VISIBLE
                        layoutImage.visibility = View.VISIBLE

                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    }catch (e:Exception){
                        Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,requireActivity())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(),perms)){
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onRationaleDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }
}