package com.example.zigzag.data

import com.example.zigzag.data.network.VideoService
import com.example.zigzag.data.network.VideoServiceResult
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import retrofit2.HttpException
import java.io.IOException

class VideoRepository(private val videoService: VideoService) {

    private val videoResult = ConflatedBroadcastChannel<VideoServiceResult>()

    suspend fun getVideoOnline(): Flow<VideoServiceResult> {
        requestVideo()
        return videoResult.asFlow()
    }

    private suspend fun requestVideo() {
        try {
            val response = videoService.fetchVideos()
            videoResult.offer(VideoServiceResult.Success(response))
        } catch (exception: IOException) {
            videoResult.offer(VideoServiceResult.Error(exception))
        } catch (exception: HttpException) {
            videoResult.offer(VideoServiceResult.Error(exception))
        }
    }
}