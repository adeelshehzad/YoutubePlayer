package com.adeel.youtubeapp.view.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.model.YoutubePlaylistResponse
import com.adeel.youtubeapp.utils.PaginationScrollListener
import com.adeel.youtubeapp.view.adapter.YoutubePlaylistAdapter
import kotlinx.android.synthetic.main.fragment_playlist.*


class PlaylistFragment : BaseFragment(R.layout.fragment_playlist), View.OnClickListener {
    override fun onClick(v: View?) {
        val playlist = v?.tag as YoutubePlaylistResponse?
        playlist?.let {
            val action = PlaylistFragmentDirections.actionLoadVideoFragment(it)
            navController.navigate(action)
        }
    }

    private lateinit var playlistAdapter: YoutubePlaylistAdapter
    private var nextPageToken: String = ""
    private var isLoading = false


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        playlistAdapter = YoutubePlaylistAdapter(arrayListOf(), this)

        rvPlaylists.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = playlistAdapter
            addOnScrollListener(object :
                PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    pbLoadingMoreItems.visibility = View.VISIBLE
                    isLoading = true
                    playerViewModel.fetchYoutubePlaylist(nextPageToken, true)
                }

                override fun isLastPage(): Boolean {
                    return TextUtils.isEmpty(nextPageToken)
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }
            })
        }

        observeViewModel(rvPlaylists, pbPlaylistLoading)
        playerViewModel.fetchYoutubePlaylist(nextPageToken, false)
    }

    override fun observeViewModel(recyclerView: RecyclerView, progressBar: ProgressBar) {
        super.observeViewModel(recyclerView, progressBar)

        playerViewModel.youtubePlaylist.observe(viewLifecycleOwner, Observer {
            isLoading = false
            pbLoadingMoreItems.visibility = View.GONE
            playlistAdapter.updatePlaylists(it)
            nextPageToken = it[it.size - 1].nextPageToken ?: ""
        })
    }
}
