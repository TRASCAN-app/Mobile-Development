package com.example.capstoneproject.Article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstoneproject.Article.Response.ArticlesItem


class ArticleModel : ViewModel() {
    private val repository = ArticleRepository() // Menggunakan repository untuk data
    val listArticles: LiveData<List<ArticlesItem>> = repository.articles

    init {
        fetchArticles() // Memanggil data saat ViewModel diinisialisasi
    }

    private fun fetchArticles() {
        repository.fetchArticles() // Ambil data dari API
    }
}