package com.adeel.youtubeapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.databinding.ItemYoutubePlaylistBinding
import com.adeel.youtubeapp.model.YoutubePlaylistResponse

class YoutubePlaylistAdapter(
    private val youtubePlaylistList: ArrayList<YoutubePlaylistResponse>,
    private val onClick: View.OnClickListener
) :
    RecyclerView.Adapter<YoutubePlaylistViewHolder>() {
    private val playlistMap = HashMap<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubePlaylistViewHolder {
        val contentView = DataBindingUtil.inflate<ItemYoutubePlaylistBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_youtube_playlist,
            parent,
            false
        )
        return YoutubePlaylistViewHolder(contentView)
    }

    override fun getItemCount(): Int = youtubePlaylistList.size

    override fun onBindViewHolder(holder: YoutubePlaylistViewHolder, position: Int) {
        val playlist = youtubePlaylistList[holder.adapterPosition]

        holder.view.youtubePlaylist = playlist

        holder.itemView.tag = playlist
        holder.itemView.setOnClickListener(onClick)
    }

    fun updatePlaylists(updatedList: List<YoutubePlaylistResponse>) {
        updatedList.forEach {
            if(!playlistMap.containsKey(it.playlistId)) {
                youtubePlaylistList.add(it)
                playlistMap[it.playlistId] = true
            }
        }
        notifyDataSetChanged()
    }
}

class YoutubePlaylistViewHolder(val view: ItemYoutubePlaylistBinding) :
    RecyclerView.ViewHolder(view.root)