package com.ayhanunal.routeapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.ayhanunal.routeapp.model.Locations
import android.graphics.Color
import android.graphics.Color.BLUE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.ayhanunal.routeapp.R
import kotlinx.android.synthetic.main.row_place.view.*

class LocationsAdapter(private val locationsList: ArrayList<Locations>) : RecyclerView.Adapter<LocationsAdapter.RowHolder>() {

    class RowHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_place,parent,false)
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
        val priorityRating = holder.itemView.findViewById<RatingBar>(R.id.row_place_priority_rating_bar)
        val mainLayout = holder.itemView.findViewById<LinearLayout>(R.id.row_place_main_layout)
        val isActiveText = holder.itemView.findViewById<TextView>(R.id.row_place_not_active_text)
        val addressText = holder.itemView.findViewById<TextView>(R.id.row_place_address_text)
        val priceText = holder.itemView.findViewById<TextView>(R.id.row_place_price_text)

        val location = locationsList[position]
        holder.itemView.apply {
            nameText.text = location.name
            descText.text = location.description
            numberText.text = (position + 1).toString()
            distanceText.text = "%.1f KM".format((location.distance)/1000.0)
            estimatedText.text = "%.2f Hours".format((location.distance)/100000.0)
            priorityRating.rating = location.priority.toFloat()
            addressText.text = location.address
            priceText.text = if (location.price != "0") "${location.price} TL" else "Free"


            //simdilik calismasin
            /*
            if (!location.isActive){
                isActiveText.visibility = View.VISIBLE
            }

             */

        }
    }

}