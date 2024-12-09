package com.example.capstoneproject.Maps

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject.R


class LocationAdapter(
    private val locations: List<LocationsItem>,
    private val onItemClicked: (LocationsItem) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvDistance: TextView = view.findViewById(R.id.tvDistance)

        fun bind(location: LocationsItem) {
            val decimalFormat = DecimalFormat("#.#")
            val formattedDistance = decimalFormat.format(location.distanceKm)
            tvName.text = location.name
            tvAddress.text = location.address
            tvDistance.text = "${formattedDistance} km"

            itemView.setOnClickListener {
                onItemClicked(location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maps, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int = locations.size
}