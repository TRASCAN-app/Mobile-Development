package com.example.capstoneproject.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig
{
    companion object{
        fun getApiService():ApiService{
            val loggingInterceptor=HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true) // Aktifkan retry
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
            val retrofit= retrofit2.Retrofit.Builder()
                .baseUrl("https://trascan-app-be-175588523136.asia-east1.run.app/")

                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}