package hiutrun.example.notesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import hiutrun.example.notesapp.adapter.NotesAdapter
import hiutrun.example.notesapp.database.NotesDatabase
import hiutrun.example.notesapp.entities.Notes
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {

    var arrNotes = ArrayList<Notes>()
    var notesAdapter: NotesAdapter = NotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        launch {
            context?.let {
                var notes = NotesDatabase.getDatabase(it).noteDao().getAllNotes()
                notesAdapter!!.setData(notes)
                recycler_view.adapter = notesAdapter
                arrNotes = notes as ArrayList<Notes>
            }
        }

        notesAdapter!!.setOnClickListener(onClicked)

        fabBtnCreateNote.setOnClickListener{
            replaceFragment(CreateNoteFragment.newInstance(),false)
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var tempArr = ArrayList<Notes>()
                for (item in arrNotes){
                    if(item.title!!.toLowerCase(Locale.getDefault()).contains(newText.toString())){
                        tempArr.add(item)

                    }
                }
                notesAdapter.setData(tempArr)
                notesAdapter.notifyDataSetChanged()
                return true
            }
        })
    }

    private val onClicked = object : NotesAdapter.OnItemClickListener{
        override fun onClicked(notesId: Int) {

            var fragment:Fragment
            var bundle = Bundle()
            bundle.putInt("noteId", notesId)
            fragment = CreateNoteFragment.newInstance()
            fragment.arguments = bundle
            replaceFragment(fragment,false)
        }


    }

    private fun replaceFragment(fragment: Fragment, isTransition : Boolean) {
        val fragmentTransition = activity!!.supportFragmentManager.beginTransaction()

        if(isTransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout,fragment).addToBackStack(fragment.javaClass.simpleName)
        fragmentTransition.commit()
    }
}