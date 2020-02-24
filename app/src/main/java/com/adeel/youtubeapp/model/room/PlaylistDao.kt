package com.adeel.youtubeapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adeel.youtubeapp.model.YoutubePlaylistResponse

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg playlist: YoutubePlaylistResponse)

    @Query("SELECT * FROM youtube_playlist")
    suspend fun getAllPlaylist(): List<YoutubePlaylistResponse>

    @Query("DELETE FROM youtube_playlist")
    suspend fun deleteAllPlaylist()
}