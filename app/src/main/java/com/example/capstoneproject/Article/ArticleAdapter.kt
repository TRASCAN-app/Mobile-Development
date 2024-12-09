package com.example.capstoneproject.Article

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstoneproject.Article.Response.ArticlesItem
import com.example.capstoneproject.R
import com.example.capstoneproject.databinding.ItemArticleBinding

class ArticleAdapter : ListAdapter<ArticlesItem, ArticleAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticlesItem) {
            binding.tvArticleTitle.text = item.title
            binding.tvDescription.text = item.description

            // Load gambar menggunakan Glide
            Glide.with(binding.imageArticle.context)
                .load(item.image)
                .placeholder(R.drawable.error_image) // Placeholder jika gambar tidak ada
                .into(binding.imageArticle)

            // Klik untuk membuka URL di browser
            binding.root.setOnClickListener {
                val context = binding.root.context
                AlertDialog.Builder(context)
                    .setTitle("Buka Artikel")
                    .setMessage("Apakah anda yakin ingin membuka artikel ini di browser?")
                    .setPositiveButton("Ya") { _, _ ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                        context.startActivity(intent)
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.url == newItem.url // Bandingkan berdasarkan URL unik
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}