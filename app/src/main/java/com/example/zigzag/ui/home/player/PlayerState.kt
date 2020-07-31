package com.example.zigzag.ui.home.player

data class PlayerState(
    var playWhenReady: Boolean,
    var currentWindow: Int,
    var playbackPosition: Long
)