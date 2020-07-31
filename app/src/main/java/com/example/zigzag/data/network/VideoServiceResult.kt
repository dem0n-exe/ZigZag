package com.example.zigzag.data.network

sealed class VideoServiceResult {
    data class Success(val data: VideoServiceResponse) : VideoServiceResult()
    data class Error(val error: Exception) : VideoServiceResult()
}