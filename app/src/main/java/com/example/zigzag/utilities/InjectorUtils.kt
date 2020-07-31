package com.example.zigzag.utilities

import com.example.zigzag.data.VideoRepository
import com.example.zigzag.data.network.VideoService
import com.example.zigzag.ui.home.HomeViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
object InjectorUtils {
    fun provideHomeViewModelFactory(): HomeViewModelFactory {
        val repository = VideoRepository(VideoService.create())
        return HomeViewModelFactory(repository)
    }
}