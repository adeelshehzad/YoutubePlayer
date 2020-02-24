package com.adeel.youtubeapp.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeel.youtubeapp.model.VideoInfo
import com.adeel.youtubeapp.model.YoutubePlaylistRepository
import com.adeel.youtubeapp.model.YoutubePlaylistResponse
import com.adeel.youtubeapp.utils.CacheManager
import com.adeel.youtubeapp.utils.KEY_GOOGLE_ACCOUNT
import com.adeel.youtubeapp.utils.SharedPreferencesHelper
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube

class YoutubePlayerViewModel(application: Application) : BaseViewModel(application) {
    private var refreshTime = 60 * 1000 * 1000 * 1000L

    private val googleCredential by lazy { CacheManager.get(KEY_GOOGLE_ACCOUNT) as GoogleAccountCredential }
    private val youtubeService by lazy {
        YouTube.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            googleCredential
        ).setApplicationName("YouTube Player")
            .build()
    }
    private val youtubePlaylistRepository by lazy { YoutubePlaylistRepository(youtubeService) }

    private val _youtubePlaylist = MutableLiveData<List<YoutubePlaylistResponse>>()
    val youtubePlaylist: LiveData<List<YoutubePlaylistResponse>>
        get() = _youtubePlaylist

    private val _playlistVideos = MutableLiveData<List<VideoInfo>>()
    val playlistVideos: LiveData<List<VideoInfo>>
        get() = _playlistVideos

    private val _searchResult = MutableLiveData<List<VideoInfo>>()
    val searchResult: LiveData<List<VideoInfo>>
        get() = _searchResult

    fun fetchYoutubePlaylist(nextPageToken: String, loadingMore: Boolean) {
        val updateTime: Long? =
            SharedPreferencesHelper.getInstance(getApplication()).playlistUpdateTime
        if (TextUtils.isEmpty(nextPageToken) && updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchPlaylistFromCache(_youtubePlaylist)
        } else {
            getResponse(
                youtubePlaylistRepository.getYoutubePlaylists(nextPageToken),
                _youtubePlaylist,
                "",
                loadingMore,
                ""
            )
        }
    }

    fun fetchPlaylistVideos(playlistId: String, nextPageToken: String, loadingMore: Boolean) {
        val updateTimeAndPlaylistId: String? =
            SharedPreferencesHelper.getInstance(getApplication()).videoUpdateTime
        val timeAndPlaylistIds = updateTimeAndPlaylistId?.split("|")
        val updateTime = timeAndPlaylistIds?.get(0)?.toLong() ?: 0L
        val savedPlaylistId = getSavedPlaylistId(timeAndPlaylistIds)

        if (TextUtils.isEmpty(nextPageToken) && updateTime != 0L && System.nanoTime() - updateTime < refreshTime && checkForPlaylistId(
                playlistId,
                timeAndPlaylistIds
            )
        ) {
            fetchVideosFromCache(playlistId, _playlistVideos)
        } else {
            getResponse(
                youtubePlaylistRepository.getPlaylistVideos(playlistId, nextPageToken),
                _playlistVideos,
                playlistId,
                loadingMore,
                savedPlaylistId
            )
        }
    }

    fun getSearchResult(query: String, nextPageToken: String, loadingMore: Boolean) {
        getResponse(
            youtubePlaylistRepository.getSearchResults(query, nextPageToken),
            _searchResult,
            "",
            loadingMore,
            ""
        )
    }

    private fun checkForPlaylistId(playlistId: String, timeAndPlaylistIds: List<String>?): Boolean {
        timeAndPlaylistIds?.forEach {
            if (it == playlistId)
                return true
        }

        return false
    }

    private fun getSavedPlaylistId(timeAndPlaylistIds: List<String>?): String {
        var savedPlaylistIds = ""
        timeAndPlaylistIds?.forEach {
            savedPlaylistIds = "$it|"
        }

        return savedPlaylistIds
    }
}
