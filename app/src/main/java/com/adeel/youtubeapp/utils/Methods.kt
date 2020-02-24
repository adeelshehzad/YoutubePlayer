package com.adeel.youtubeapp.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.adeel.youtubeapp.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat

fun isDeviceOnline(activity: Activity): Boolean {
    val connMgr =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.activeNetworkInfo

    if (networkInfo == null || !networkInfo.isConnected) {
        //showDialog
        return false
    }

    return true
}

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 15f
        start()
    }
}

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable) {
    val options =
        RequestOptions().placeholder(progressDrawable).error(R.drawable.ic_playlist_thumbnail_error)
    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this)
}

@BindingAdapter("android:thumbnail")
fun loadImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}

@BindingAdapter("android:videoDuration")
fun setVideoDuration(textView: TextView, duration: String?) {
    duration?.let {
        if(!TextUtils.isEmpty(duration)) {
            val videoDuration = formatTime(it.substring(2), "mm'M'ss'S'", "mm:ss")
            textView.text = "Duration: $videoDuration"
        }
    }
}

fun formatTime(time: String, inputFormat: String, outputFormat: String): String {
    val inputSdf = SimpleDateFormat(inputFormat)
    val outputSdf = SimpleDateFormat(outputFormat)
    try {
        val date = inputSdf.parse(time)
        return outputSdf.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}