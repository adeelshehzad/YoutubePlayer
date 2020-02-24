package com.adeel.youtubeapp.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.model.VideoInfo
import com.adeel.youtubeapp.model.YoutubePlaylistResponse
import com.adeel.youtubeapp.utils.PaginationScrollListener
import com.adeel.youtubeapp.utils.loadImage
import com.adeel.youtubeapp.view.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.fragment_videos.*

class VideosFragment : BaseFragment(R.layout.fragment_videos), View.OnClickListener {
    override fun onClick(v: View?) {
        val videoInfo = v?.tag as VideoInfo?

        videoInfo?.let {
            val action =
                VideosFragmentDirections.actionLoadPlaybackFragmentFromVideo("", it.videoId)
            navController.navigate(action)
        }
    }

    private lateinit var videoListAdapter: VideoListAdapter
    private var playlistId = ""
    private var nextPageToken = ""
    private var isLoading = false

    private var playlistInfo: YoutubePlaylistResponse? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        videoListAdapter = VideoListAdapter(arrayListOf(), this)
        observeViewModel(rvVideos, pbVideoLoading)

        rvVideos.apply {
            layoutManager = LinearLayoutManager(activity!!)
            adapter = videoListAdapter
            addOnScrollListener(object :
                PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    pbLoadingMoreItems.visibility = View.VISIBLE
                    isLoading = true
                    playerViewModel.fetchPlaylistVideos(playlistId, nextPageToken, true)
                }

                override fun isLastPage(): Boolean {
                    return TextUtils.isEmpty(nextPageToken)
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }
            })
        }

        arguments?.let {
            playlistInfo = VideosFragmentArgs.fromBundle(it).playlistInfo
        }

        playlistInfo?.let {
            loadImage(ivThumbnail, it.playlistThumbnail)
            tvPlaylistTitle.text = it.playlistName
            tvVideosCount.text = "${it.totalVideosCount} video(s)"
            playlistId = it.playlistId
            playerViewModel.fetchPlaylistVideos(playlistId, nextPageToken, false)
        }

        btPlayAll.setOnClickListener {
            val action =
                VideosFragmentDirections.actionLoadPlaybackFragmentFromVideo(playlistId, "")
            navController.navigate(action)
        }
    }

    override fun observeViewModel(recyclerView: RecyclerView, progressBar: ProgressBar) {
        super.observeViewModel(recyclerView, progressBar)

        playerViewModel.playlistVideos.observe(viewLifecycleOwner, Observer { response ->
            isLoading = false
            pbLoadingMoreItems.visibility = View.GONE
            videoListAdapter.updateVideos(response)
            nextPageToken = response[response.size - 1].nextPageToken ?: ""
        })
    }
}
