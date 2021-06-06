package com.ayhanunal.routeapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.ayhanunal.routeapp.model.Locations
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ayhanunal.routeapp.R
import kotlinx.android.synthetic.main.custom_place_row.view.*

class LocationsAdapter(private val locationsList: ArrayList<Locations>) : RecyclerView.Adapter<LocationsAdapter.RowHolder>() {

    class RowHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_place_row,parent,false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return locationsList.count()
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        val nameText = holder.itemView.findViewById<TextView>(R.id.row_place_name_text)
        val descText = holder.itemView.findViewById<TextView>(R.id.row_place_description_text)
        val numberText = holder.itemView.findViewById<TextView>(R.id.row_place_number_text)
        val distanceText = holder.itemView.findViewById<TextView>(R.id.row_place_distance_text)
        val estimatedText = holder.itemView.findViewById<TextView>(R.id.row_place_time_text)
        val priorityText = holder.itemView.findViewById<TextView>(R.id.row_place_priority_text)

        val location = locationsList[position]
        holder.itemView.apply {
            nameText.text = location.name
            descText.text = location.description
            numberText.text = (position + 1).toString()
            distanceText.text = ""
            estimatedText.text = ""
            priorityText.text = "${location.priority} / 5"
        }
    }

}