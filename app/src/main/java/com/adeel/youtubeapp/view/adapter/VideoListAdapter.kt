package com.adeel.youtubeapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.databinding.ItemPlaylistVideoBinding
import com.adeel.youtubeapp.model.VideoInfo

class VideoListAdapter(
    private val videosList: ArrayList<VideoInfo>,
    private val onClick: View.OnClickListener
) : RecyclerView.Adapter<VideoViewHolder>() {
    private val videosMap = HashMap<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val contentView = DataBindingUtil.inflate<ItemPlaylistVideoBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_playlist_video,
            parent,
            false
        )
        return VideoViewHolder(contentView)
    }

    override fun getItemCount(): Int = videosList.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoInfo = videosList[holder.adapterPosition]

        holder.view.videoInfo = videoInfo
        holder.itemView.tag = videoInfo
        holder.itemView.setOnClickListener(onClick)
    }

    fun updateVideos(updatedList: List<VideoInfo>) {
        updatedList.forEach {
            if (!videosMap.containsKey(it.videoId)) {
                videosList.add(it)
                videosMap[it.videoId] = true
            }
        }
        notifyDataSetChanged()
    }
}

class VideoViewHolder(val view: ItemPlaylistVideoBinding) :
    RecyclerView.ViewHolder(view.root)