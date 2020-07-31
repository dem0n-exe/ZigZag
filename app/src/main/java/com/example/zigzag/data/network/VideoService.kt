package com.example.zigzag.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface VideoService {
    @GET("demo/video/list/samples.json")
    suspend fun fetchVideos(): VideoServiceResponse

    companion object {
        private const val BASE_URL = "https://res.cloudinary.com/"

        fun create(): VideoService {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VideoService::class.java)
        }
    }
}