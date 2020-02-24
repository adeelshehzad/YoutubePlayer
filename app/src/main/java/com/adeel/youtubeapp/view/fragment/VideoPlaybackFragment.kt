package com.adeel.youtubeapp.view.fragment


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.utils.LOGTAG_YOUTUBE_PLAYER
import com.adeel.youtubeapp.utils.YOUTUBE_API_KEY
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment

class VideoPlaybackFragment : BaseFragment(R.layout.fragment_video_playback) {

    private var playlistId: String? = null
    private var videoId: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            playlistId = VideoPlaybackFragmentArgs.fromBundle(it).playlistId
            videoId = VideoPlaybackFragmentArgs.fromBundle(it).videoId
            initiallizeYoutubeFragment()
        }
    }

    private fun initiallizeYoutubeFragment() {
        val youtubePlayerFragment = YouTubePlayerSupportFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentYoutubePlayer, youtubePlayerFragment as Fragment)
            .commit()

        youtubePlayerFragment.initialize(
            YOUTUBE_API_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer?,
                    p2: Boolean
                ) {
                    Log.d(LOGTAG_YOUTUBE_PLAYER, "YouTube player initialized")
                    p1?.let {
                        if (!TextUtils.isEmpty(playlistId)) {
                            it.loadPlaylist(playlistId)
                        } else if (!TextUtils.isEmpty(videoId)) {
                            it.loadVideo(videoId)
                        }
                    }
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    Log.d(LOGTAG_YOUTUBE_PLAYER, "Failed to initialize player")
                }
            })
    }
}
