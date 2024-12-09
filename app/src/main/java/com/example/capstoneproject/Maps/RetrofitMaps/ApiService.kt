package com.example.capstoneproject.Maps.RetrofitMaps

import com.example.capstoneproject.Maps.ResponseMaps
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/recycling-locations")

    fun getLocations(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("radius") radius: Int
    ): Call<ResponseMaps>

}