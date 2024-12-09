package com.example.capstoneproject.ApiPredict

import com.google.gson.annotations.SerializedName

data class ResponsePredict(

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("score")
	val score: Any,

	@field:SerializedName("recyclable")
	val recyclable: Boolean,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("waste_type")
	val wasteType: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("message")
	val message: String
)
data class ResponsePredictError(

	@field:SerializedName("error")
	val error: String
)

