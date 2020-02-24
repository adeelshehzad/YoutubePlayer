package com.adeel.youtubeapp.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adeel.youtubeapp.model.VideoInfo
import com.adeel.youtubeapp.model.YoutubePlaylistResponse

@Database(
    entities = [YoutubePlaylistResponse::class, VideoInfo::class],
    version = 1,
    exportSchema = false
)
abstract class YoutubePlayerDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun videoDao(): VideoDao

    companion object {
        @Volatile
        private var instance: YoutubePlayerDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            YoutubePlayerDatabase::class.java,
            "YoutubePlayerDB"
        ).build()
    }
}