package com.adeel.youtubeapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeel.youtubeapp.model.VideoInfo

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg playlist: VideoInfo)

    @Query("SELECT * FROM video_info WHERE videoPlaylistId = :playlistId")
    suspend fun getAllVideo(playlistId: String): List<VideoInfo>

    @Query("DELETE FROM video_info WHERE videoPlaylistId = :playlistId")
    suspend fun deleteAllVideo(playlistId: String)
}