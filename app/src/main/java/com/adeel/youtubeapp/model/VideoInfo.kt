package com.adeel.youtubeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_info")
data class VideoInfo(
    var thumbnail: String?,
    var title: String?,
    var author: String?,
    var duration: String?,
    @PrimaryKey
    var videoId: String,
    var nextPageToken: String?
) {

    var videoPlaylistId: String = ""
}