package com.example.zigzag.ui.home.player

import android.content.Context
import android.net.Uri
import com.danikula.videocache.CacheListener
import com.danikula.videocache.HttpProxyCacheServer
import com.example.zigzag.R
import com.example.zigzag.utilities.CacheUtils
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.ShuffleOrder
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.File


class MediaPlayerImplementation private constructor(private val context: Context) : CacheListener {
    private lateinit var proxy: HttpProxyCacheServer

    val player: SimpleExoPlayer

    init {
        player = initializePlayer()
    }

    companion object {
        @Volatile
        private var INSTANCE: MediaPlayerImplementation? = null

        fun getInstance(context: Context): MediaPlayerImplementation {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val newInstance = MediaPlayerImplementation(context)
                INSTANCE = newInstance
                return newInstance
            }
        }
    }

    private fun initializePlayer(): SimpleExoPlayer {
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(context, videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl()
        val rendersFactory = DefaultRenderersFactory(context)

        return SimpleExoPlayer.Builder(context, rendersFactory)
            .setBandwidthMeter(bandwidthMeter)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build()
    }

    fun play(
        playerState: PlayerState,
        urls: List<String>
    ) {
        player.prepare(buildMediaSourcePlaylist(urls), false, false)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.seekTo(playerState.currentWindow, playerState.playbackPosition)
        player.playWhenReady = playerState.playWhenReady
    }


    private fun buildMediaSource(url: String): MediaSource {
        setProxy(url)
        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        return ProgressiveMediaSource
            .Factory(DefaultDataSourceFactory(context, userAgent))
            .createMediaSource(Uri.parse(proxy.getProxyUrl(url)))
    }

    private fun buildMediaSourcePlaylist(urls: List<String>): MediaSource {
        val concatenatingMediaSource =
            ConcatenatingMediaSource(
                false,
                true,
                ShuffleOrder.DefaultShuffleOrder(urls.size - 1),
                buildMediaSource(urls[1])
            )

        for (i in 2..urls.lastIndex) {
            concatenatingMediaSource.addMediaSource(buildMediaSource(urls[i]))
        }

        return concatenatingMediaSource
    }

    fun releasePlayer() {
        player.release()
        if (this::proxy.isInitialized) proxy.unregisterCacheListener(this)
    }

    override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {}

    private fun setProxy(url: String) {
        proxy = HttpProxyCacheServer.Builder(context.applicationContext)
            .cacheDirectory(CacheUtils.getVideoCacheDir(context.applicationContext))
            .build()
        proxy.registerCacheListener(this, url)
    }


}