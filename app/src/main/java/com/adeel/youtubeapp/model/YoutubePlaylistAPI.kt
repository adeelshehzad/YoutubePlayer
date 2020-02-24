package com.adeel.youtubeapp.model

import android.text.TextUtils
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.SearchResult
import com.google.api.services.youtube.model.Video
import io.reactivex.Observable

class YoutubePlaylistAPI(private val youtubeService: YouTube) {
    private val part = "snippet,contentDetails"

    fun getUsersPlaylists(nextPageToken: String): Observable<List<YoutubePlaylistResponse>> {
        return Observable.create { emitter ->
            try {
                val result = youtubeService.playlists().list(part)
                    .setMine(true)
                    .setPageToken(nextPageToken)
                    .setMaxResults(8L)
                    .execute()

                if (result.items.isEmpty())
                    throw Exception("Playlist empty")

                emitter.onNext(getPlaylistResponse(result.items, result.nextPageToken ?: ""))
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun getPlaylistVideos(playlistId: String, nextPageToken: String): Observable<List<VideoInfo>> {
        return Observable.create { emitter ->
            try {
                val playlistVidoesList = youtubeService.playlistItems().list(part)
                    .setPlaylistId(playlistId)
                    .setPageToken(nextPageToken)
                    .setMaxResults(10L)
                    .execute()

                if (playlistVidoesList.items.isEmpty())
                    throw Exception("Playlist empty")

                val videoIds = getVideoIds(playlistVidoesList.items)

                val videosList = getVideosList(videoIds)
                emitter.onNext(
                    getVideosInfo(
                        videosList,
                        playlistId,
                        playlistVidoesList.nextPageToken ?: ""
                    )
                )
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun getSearchResult(nextPageToken: String, query: String): Observable<List<VideoInfo>> {
        return Observable.create { emitter ->
            try {
                val searchResponse = youtubeService.search().list("snippet")
                    .setPageToken(nextPageToken)
                    .setMaxResults(10L)
                    .setQ(query)
                    .execute()

                if (searchResponse.items.isEmpty())
                    throw Exception("No results")

                val videosList =
                    getSearchVideosInfo(searchResponse.items, searchResponse.nextPageToken ?: "")
                emitter.onNext(videosList)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    private fun getVideosList(videoId: String): List<Video> {
        val result = youtubeService.videos().list(part)
            .setId(videoId)
            .setMaxResults(20L)
            .execute()

        if (result.items.isEmpty())
            throw Exception("Playlist empty")

        return result.items
    }

    private fun getPlaylistResponse(
        items: List<Playlist>?,
        nextPageToken: String
    ): ArrayList<YoutubePlaylistResponse> {
        val youtubePlaylistLists = ArrayList<YoutubePlaylistResponse>()
        items?.forEach { playlist ->
            val youtubePlaylist = YoutubePlaylistResponse(
                playlist.contentDetails.itemCount.toInt(),
                playlist.snippet.localized.title,
                playlist.snippet.thumbnails.default.url,
                playlist.id,
                nextPageToken
            )

            youtubePlaylistLists.add(youtubePlaylist)
        }

        return youtubePlaylistLists
    }

    private fun getVideoIds(items: List<PlaylistItem>): String {
        var videoIds = ""
        items.forEach {
            videoIds = "${it.contentDetails.videoId},$videoIds"
        }

        return videoIds
    }

    private fun getVideosInfo(
        items: List<Video>,
        playlistId: String,
        nextPageToken: String
    ): ArrayList<VideoInfo> {
        val videosInfoList = ArrayList<VideoInfo>()
        items.forEach { video ->
            val videoInfo = VideoInfo(
                video.snippet.thumbnails.default.url,
                video.snippet.localized.title,
                video.snippet.channelTitle,
                video.contentDetails.duration,
                video.id,
                nextPageToken
            )
            videoInfo.videoPlaylistId = playlistId

            videosInfoList.add(videoInfo)
        }

        return videosInfoList
    }

    private fun getSearchVideosInfo(
        items: List<SearchResult>,
        nextPageToken: String
    ): ArrayList<VideoInfo> {
        val videosInfoList = ArrayList<VideoInfo>()
        items.forEach { video ->
            if (!TextUtils.isEmpty(video.id.videoId)) {
                val videoInfo = VideoInfo(
                    video.snippet.thumbnails.default.url,
                    video.snippet.title,
                    video.snippet.channelTitle,
                    "",
                    video.id.videoId,
                    nextPageToken
                )
                videosInfoList.add(videoInfo)
            }
        }

        return videosInfoList
    }
}