package com.adeel.youtubeapp.view.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.model.VideoInfo
import com.adeel.youtubeapp.utils.PaginationScrollListener
import com.adeel.youtubeapp.view.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(R.layout.fragment_search), View.OnClickListener {
    override fun onClick(v: View?) {
        val videoInfo = v?.tag as VideoInfo?

        videoInfo?.let {
            val action =
                SearchFragmentDirections.actionLoadPlaybackFragmentFromSearch("", it.videoId)
            navController.navigate(action)
        }
    }

    private lateinit var videosListAdapter: VideoListAdapter

    private var nextPageToken: String = ""
    private var isLoading = false
    private var searchQuery = "";
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        videosListAdapter = VideoListAdapter(arrayListOf(), this)

        rvSearch.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = videosListAdapter
            addOnScrollListener(object :
                PaginationScrollListener(layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    pbLoadingMoreItems.visibility = View.VISIBLE
                    isLoading = true
                    playerViewModel.getSearchResult(searchQuery, nextPageToken, true)
                }

                override fun isLastPage(): Boolean {
                    return TextUtils.isEmpty(nextPageToken)
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }
            })

        }

        observeViewModel(rvSearch, pbSearchLoading)

        btSearch.setOnClickListener {
            searchVideos()
        }

        etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchVideos()
            }

            false
        }
    }

    private fun searchVideos() {
        searchQuery = etSearchQuery.text.toString()
        if (!TextUtils.isEmpty(searchQuery)) {
            playerViewModel.getSearchResult(searchQuery, "", false)
        } else {
            Toast.makeText(activity, "Search text cannot be empty", Toast.LENGTH_LONG).show()
        }
    }

    override fun observeViewModel(recyclerView: RecyclerView, progressBar: ProgressBar) {
        super.observeViewModel(recyclerView, progressBar)

        playerViewModel.searchResult.observe(viewLifecycleOwner, Observer {
            isLoading = false
            pbLoadingMoreItems.visibility = View.GONE
            videosListAdapter.updateVideos(it)
            nextPageToken = it[it.size - 1].nextPageToken ?: ""
        })
    }

}
