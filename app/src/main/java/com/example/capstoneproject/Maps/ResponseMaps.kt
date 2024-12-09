package com.example.capstoneproject.Maps

import com.google.gson.annotations.SerializedName

data class ResponseMaps(

	@field:SerializedName("locations")
	val locations: List<LocationsItem>
)

data class LocationsItem(

	@field:SerializedName("distance_km")
	val distanceKm: Any,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("name")
	val name: String
)
