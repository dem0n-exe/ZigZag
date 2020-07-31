package com.example.zigzag.data.network

import com.example.zigzag.data.Video
import com.google.gson.annotations.SerializedName

data class VideoServiceResponse(
    @SerializedName("resources") val videos: List<Video>
)