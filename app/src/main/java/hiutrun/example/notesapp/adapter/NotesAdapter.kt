package hiutrun.example.notesapp.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hiutrun.example.notesapp.R
import hiutrun.example.notesapp.entities.Notes
import kotlinx.android.synthetic.main.item_rv_home.view.*

class NotesAdapter(): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    private var arrList = ArrayList<Notes>()
    var listener:OnItemClickListener?=null

    inner class NotesViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_rv_home,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return arrList.size
    }


    fun setData(arrNotesList: List<Notes>){
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener){
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.itemView.tvTitle.text = arrList[position].title;
        holder.itemView.tvDesc.text = arrList[position].noteText;
        holder.itemView.tvDateTime.text = arrList[position].dateTime;

        if(arrList[position].color!=null){
            holder.itemView.cardView.setBackgroundColor(Color.parseColor(arrList[position].color))
        }else{
            holder.itemView.cardView.setCardBackgroundColor(Color.parseColor(R.color.ColorLightBlack.toString()))
        }

        if(arrList[position].img !=null){
            holder.itemView.imgNote.setImageBitmap(BitmapFactory.decodeFile(arrList[position].img))
            holder.itemView.imgNote.visibility = View.VISIBLE

        }else{
            holder.itemView.imgNote.visibility = View.GONE
        }

        if(arrList[position].webLink !=null){
            holder.itemView.tvWebLink.text = arrList[position].webLink
            holder.itemView.tvWebLink.visibility = View.VISIBLE
        }else{
            holder.itemView.tvWebLink.visibility = View.GONE
        }

        holder.itemView.cardView.setOnClickListener {
            arrList[position].id?.let { it1 -> listener!!.onClicked(it1) }
        }
    }

    interface OnItemClickListener {
        fun onClicked(notesId:Int)
    }


}