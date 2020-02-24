package com.adeel.youtubeapp.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper {
    companion object {
        private val sharePref = SharedPreferencesHelper()
        private lateinit var sharedPreferences: SharedPreferences

        fun getInstance(context: Context): SharedPreferencesHelper {
            if (!::sharedPreferences.isInitialized) {
                synchronized(SharedPreferencesHelper::class.java) {
                    if (!::sharedPreferences.isInitialized) {
                        sharedPreferences =
                            context.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE)
                    }
                }
            }
            return sharePref
        }
    }

    val playlistUpdateTime: Long?
        get() = sharedPreferences.getLong(PREF_PLAYLIST_UPDATE_TIME, 0L)

    val videoUpdateTime: String?
        get() = sharedPreferences.getString(PREF_VIDEO_UPDATE_TIME, "0|")

    val accountName: String?
        get() = sharedPreferences.getString(PREF_ACCOUNT_NAME, "")

    fun savePlaylistUpdateTime(time: Long) {
        sharedPreferences.edit()
            .putLong(PREF_PLAYLIST_UPDATE_TIME, time)
            .apply()
    }

    fun saveVideoUpdateTime(timeAndPlaylistId: String) {
        sharedPreferences.edit()
            .putString(PREF_VIDEO_UPDATE_TIME, timeAndPlaylistId)
            .apply()
    }

    fun saveAccountName(accountName: String) {
        sharedPreferences.edit().putString(PREF_ACCOUNT_NAME, accountName).apply()
    }

}