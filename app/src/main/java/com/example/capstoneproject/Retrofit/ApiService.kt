package com.example.capstoneproject.Retrofit

import com.example.capstoneproject.ApiPredict.ResponsePredict
import com.example.capstoneproject.Article.Response.ResponseArticle
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/articles")
    fun getArticles(): Call<ResponseArticle>

    @Multipart
    @POST("/predict")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ):Call<ResponsePredict>


}