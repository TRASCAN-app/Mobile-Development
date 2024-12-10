package com.example.capstoneproject


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.capstoneproject.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageView = binding.tvResultImage

        // Ambil data dari Intent
        val result = intent.getStringExtra("result")
        val score = intent.getStringExtra("score")
        val recyclable = intent.getBooleanExtra("recyclable", false)
        val imageUrl = intent.getStringExtra("image_url")
        if (imageUrl.isNullOrEmpty()) {
            // Handle jika imageUrl kosong atau null
            imageView.setImageResource(R.drawable.error_image)
        }
        Log.d("ImageURL", "URL: $imageUrl")
        val wasteType = intent.getStringExtra("waste_type")

        // Tampilkan data ke UI
        if (result != null && imageUrl != null) {

            val formattedResult = formatResult(result)

            binding.tvResult.text = "Result: $formattedResult"
            binding.tvScore.text = "Score: $score"
            binding.tvRecyclable.text = "Recyclable: ${if (recyclable) "Yes" else "No"}"
            binding.tvWasteType.text = "Waste Type: ${formatResult(wasteType ?: "")}"

            // Load image menggunakan Glide
            Glide.with(this)
                .load(imageUrl)
                .into(binding.tvResultImage)
        } else {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            finish()
        }


        // Tombol kembali
        binding.btnBack.setOnClickListener {
            finish()
        }

    }
    private fun formatResult(input: String): String {
        return input.split("_") // Pisahkan berdasarkan underscore
            .joinToString(" ") { it.capitalize() } // Ubah setiap kata menjadi kapital dan gabungkan
    }
}