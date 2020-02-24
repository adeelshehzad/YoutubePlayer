package com.adeel.youtubeapp.model.room

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class PlaylistEntity(
    @ColumnInfo(name = "playlist_response")
    val playlistListResponse: String
) {
    @PrimaryKey(autoGenerate = true)
    var pl_id: Int = 0
}