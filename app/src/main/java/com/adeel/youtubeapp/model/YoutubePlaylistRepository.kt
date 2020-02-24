package com.adeel.youtubeapp.model

import com.google.api.services.youtube.YouTube

class YoutubePlaylistRepository(youtubeService: YouTube) {
    private val youtubePlaylistAPI = YoutubePlaylistAPI(youtubeService)

    fun getYoutubePlaylists(nextPageToken: String) =
        youtubePlaylistAPI.getUsersPlaylists(nextPageToken)

    fun getPlaylistVideos(playlistId: String, nextPageToken: String) =
        youtubePlaylistAPI.getPlaylistVideos(playlistId, nextPageToken)

    fun getSearchResults(query: String, nextPageToken: String) =
        youtubePlaylistAPI.getSearchResult(nextPageToken, query)
}