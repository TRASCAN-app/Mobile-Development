package com.example.capstoneproject.Maps


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject.Maps.RetrofitMaps.ApiConfig
import com.example.capstoneproject.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getUserLocation()

        return view
    }

    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                getLocationsFromApi(location.latitude, location.longitude, 1000)
            }
        }
    }

    private fun getLocationsFromApi(lat: Double, lon: Double, radius: Int) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getLocations(lat, lon, radius)

        call.enqueue(object : Callback<ResponseMaps> {
            override fun onResponse(call: Call<ResponseMaps>, response: Response<ResponseMaps>) {
                if (response.isSuccessful) {
                    val locations = response.body()?.locations ?: emptyList()
                    locationAdapter = LocationAdapter(locations) { location ->
                        openGoogleMaps(location)
                    }
                    recyclerView.adapter = locationAdapter
                }
            }

            override fun onFailure(call: Call<ResponseMaps>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openGoogleMaps(location: LocationsItem) {
        val uri = Uri.parse("geo:0,0?q=${location.address}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }
}