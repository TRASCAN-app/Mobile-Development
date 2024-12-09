package com.example.capstoneproject.Article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneproject.Article.Response.ArticlesItem
import com.example.capstoneproject.R

class ArticleFragment : Fragment() {
    private val viewModel by viewModels<ArticleModel>()
    private lateinit var adapter: ArticleAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_articles)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar_article)

        adapter=ArticleAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        viewModel.listArticles.observe(viewLifecycleOwner) { article ->
            if(article.isNotEmpty()){
                progressBar.visibility=View.GONE
                adapter.submitList(article)
            }else{
                progressBar.visibility=View.VISIBLE
            }
        }


        return view

    }


}