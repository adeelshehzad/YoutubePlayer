package com.adeel.youtubeapp.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.adeel.youtubeapp.utils.REQUEST_AUTHORIZATION
import com.adeel.youtubeapp.viewmodel.YoutubePlayerViewModel
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

open class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    lateinit var playerViewModel: YoutubePlayerViewModel

    val navController by lazy { findNavController() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        playerViewModel = ViewModelProvider(this)[YoutubePlayerViewModel::class.java]
    }

    open fun observeViewModel(recyclerView: RecyclerView, progressBar: ProgressBar) {
        playerViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) showLoading(recyclerView, progressBar)
            else hideLoading(recyclerView, progressBar)
        })

        playerViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error ->
            if (error is UserRecoverableAuthIOException) {
                startActivityForResult(
                    error.intent,
                    REQUEST_AUTHORIZATION
                )
            } else {
                error.printStackTrace()
                Toast.makeText(activity, "Some error occured", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showLoading(view: RecyclerView, loadingView: ProgressBar) {
        view.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoading(view: RecyclerView, loadingView: ProgressBar) {
        view.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_AUTHORIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    playerViewModel.fetchYoutubePlaylist("",false)
                }
            }
        }
    }
}