package com.example.zigzag.data

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("public_id") val publicId: String,
    val version: Int,
    val format: String,
    val width: Int,
    val height: Int,
    val type: String,
    @SerializedName("created_at") val createdAt: String
)