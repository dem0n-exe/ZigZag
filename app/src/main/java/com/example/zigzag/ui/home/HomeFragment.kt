package com.example.zigzag.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.zigzag.R
import com.example.zigzag.ui.home.player.MediaPlayerImplementation
import com.example.zigzag.ui.home.player.OnSwipeListener
import com.example.zigzag.utilities.InjectorUtils
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class HomeFragment : Fragment(), View.OnTouchListener {

    private val homeViewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory()
    }
    private lateinit var playerView: PlayerView
    private lateinit var likeButton: ToggleButton
    private lateinit var shareButton: ImageView
    private lateinit var retryLayout: LinearLayout
    private lateinit var retryText: TextView
    private lateinit var loading: ImageView

    private lateinit var listOfUrl: List<String>
    private lateinit var mediaPlayer: MediaPlayerImplementation
    private lateinit var gestureDetector: GestureDetector

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        playerView = root.findViewById(R.id.player_view)
        likeButton = root.findViewById(R.id.switch_like)
        shareButton = root.findViewById(R.id.share)
        retryLayout = root.findViewById(R.id.layout_retry)
        retryText = root.findViewById(R.id.text_retry)
        loading = root.findViewById(R.id.loading)

        return root
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun play() {
        homeViewModel.urls.observe(viewLifecycleOwner) { urls ->
            if (urls.isEmpty()) {
                setViews(false)
            } else {
                setViews(true)
                listOfUrl = urls
                mediaPlayer.play(homeViewModel.getPlayerState(), urls)
            }
        }

    }

    private fun initPlayer() {
        mediaPlayer = MediaPlayerImplementation.getInstance(requireContext())
        playerView.player = mediaPlayer.player

        mediaPlayer.player.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                Log.d(TAG, "Player Error:${error.message!!}")
                homeViewModel.setPlayerState(mediaPlayer.player)
                setViews(false)
                super.onPlayerError(error)
            }
        })

        play()
    }

    private fun releasePlayer() {
        homeViewModel.setPlayerState(playerView.player ?: return)
        mediaPlayer.releasePlayer()
        playerView.player = null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun setViews(isEnabled: Boolean) {
        loading.visibility = View.GONE
        if (isEnabled) {
            shareButton.visibility = View.VISIBLE
            likeButton.visibility = View.VISIBLE
            playerView.visibility = View.VISIBLE
            shareButton.setOnClickListener { share() }

            val onSwipeListener: OnSwipeListener = object : OnSwipeListener() {
                override fun onSwipe(direction: Direction?): Boolean {
                    return when (direction) {
                        Direction.up -> {
                            if (mediaPlayer.player.hasNext()) {
                                mediaPlayer.player.next()
                                likeButton.isChecked = false
                            }
                            true
                        }

                        Direction.down -> {
                            if (mediaPlayer.player.hasPrevious()) {
                                mediaPlayer.player.previous()
                                likeButton.isChecked = false
                            }
                            true
                        }
                        else -> super.onSwipe(direction)
                    }
                }
            }
            gestureDetector = GestureDetector(requireContext(), onSwipeListener)
            playerView.setOnTouchListener(this)
        } else {
            playerView.visibility = View.GONE
            shareButton.visibility = View.GONE
            likeButton.visibility = View.GONE
        }
        setRetryLayout(!isEnabled)
    }

    private fun setRetryLayout(isEnabled: Boolean) {
        if (isEnabled) {
            retryLayout.visibility = View.VISIBLE
            retryText.visibility = View.VISIBLE
            retryLayout.setOnClickListener {
                homeViewModel.retry.value = !(homeViewModel.retry.value!!)
                mediaPlayer.player.retry()
                loading.visibility = View.VISIBLE
            }
        } else {
            retryLayout.visibility = View.GONE
            retryText.visibility = View.GONE
        }
    }

    private fun share() {
        if (listOfUrl.isNotEmpty()) {
            val url = listOfUrl[mediaPlayer.player.currentWindowIndex + 1]
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Saw this video on ZigZag :\n$url")
            }
            requireContext().startActivity(Intent.createChooser(intent, "Share ZigZag"))
        }
    }
}