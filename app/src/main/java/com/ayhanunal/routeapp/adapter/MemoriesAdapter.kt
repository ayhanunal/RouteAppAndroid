package com.ayhanunal.routeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayhanunal.routeapp.R
import com.ayhanunal.routeapp.model.Memories
import com.bumptech.glide.Glide

class MemoriesAdapter(private val memoriesList: ArrayList<Memories>) : RecyclerView.Adapter<MemoriesAdapter.RowHolder>() {

    class RowHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_memories,parent,false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return memoriesList.count()
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val nameText = holder.itemView.findViewById<TextView>(R.id.row_mem_name_text)
        val descText = holder.itemView.findViewById<TextView>(R.id.row_mem_description_text)
        val dateText = holder.itemView.findViewById<TextView>(R.id.row_mem_date_text)
        val priorityRating = holder.itemView.findViewById<RatingBar>(R.id.row_mem_priority_rating_bar)
        val mainLayout = holder.itemView.findViewById<LinearLayout>(R.id.row_mem_main_layout)
        val isActiveText = holder.itemView.findViewById<TextView>(R.id.row_mem_not_active_text)
        val imageView = holder.itemView.findViewById<ImageView>(R.id.row_mem_image_view)

        val memories = memoriesList[position]
        holder.itemView.apply {
            nameText.text = memories.memName
            descText.text = memories.memDescription
            dateText.text = memories.memDate
            priorityRating.rating = memories.memPriority.toFloat()
            Glide.with(this.context)
                .load(memories.memImageUrl)
                .into(imageView)

        }
    }

}