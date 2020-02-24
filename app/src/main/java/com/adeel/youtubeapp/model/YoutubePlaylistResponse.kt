package com.adeel.youtubeapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "youtube_playlist")
data class YoutubePlaylistResponse(
    @ColumnInfo(name = "total_videos_count")
    var totalVideosCount: Int?,

    @ColumnInfo(name = "playlist_name")
    var playlistName: String?,

    @ColumnInfo(name = "playlist_thumbnail")
    var playlistThumbnail: String?,

    @ColumnInfo(name = "playlist_id")
    @PrimaryKey
    var playlistId: String,

    @ColumnInfo(name = "next_page_token")
    var nextPageToken: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(totalVideosCount)
        parcel.writeString(playlistName)
        parcel.writeString(playlistThumbnail)
        parcel.writeString(playlistId)
        parcel.writeString(nextPageToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<YoutubePlaylistResponse> {
        override fun createFromParcel(parcel: Parcel): YoutubePlaylistResponse {
            return YoutubePlaylistResponse(parcel)
        }

        override fun newArray(size: Int): Array<YoutubePlaylistResponse?> {
            return arrayOfNulls(size)
        }
    }
}