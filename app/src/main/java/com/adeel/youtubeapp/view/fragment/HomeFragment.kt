package com.adeel.youtubeapp.view.fragment

import android.os.Bundle
import com.adeel.youtubeapp.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btManagePlaylists.setOnClickListener {
            navController.navigate(R.id.actionLoadPlaylistFragment)
        }

        btSearchVideos.setOnClickListener {
            navController.navigate(R.id.actionLoadSearchFragment)
        }
    }
}