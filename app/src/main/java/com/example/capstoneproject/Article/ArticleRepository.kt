package com.example.capstoneproject.Article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstoneproject.Article.Response.ArticlesItem
import com.example.capstoneproject.Article.Response.ResponseArticle
import com.example.capstoneproject.Retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleRepository {
    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> = _articles

    fun fetchArticles() {
        val client = ApiConfig.getApiService().getArticles()
        client.enqueue(object : Callback<ResponseArticle> {
            override fun onResponse(call: Call<ResponseArticle>, response: Response<ResponseArticle>) {
                if (response.isSuccessful) {
                    _articles.postValue(response.body()?.articles ?: emptyList())
                } else {
                    Log.e("ArticleRepository", "Response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseArticle>, t: Throwable) {
                Log.e("ArticleRepository", "Request failed: ${t.message}")
            }
        })
    }
}