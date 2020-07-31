package com.example.zigzag.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zigzag.data.VideoRepository

class HomeViewModelFactory(
    private val videoRepository: VideoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(
            videoRepository
        ) as T
    }
}