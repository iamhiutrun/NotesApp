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

class NotesAdapter(val arrList: List<Notes>): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    inner class NotesViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_rv_home,parent,false)
        )
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
    }

    override fun getItemCount(): Int {
        return arrList.size
    }
}