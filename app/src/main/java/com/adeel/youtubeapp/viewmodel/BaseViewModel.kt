package com.adeel.youtubeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeel.youtubeapp.model.VideoInfo
import com.adeel.youtubeapp.model.YoutubePlaylistResponse
import com.adeel.youtubeapp.model.room.YoutubePlayerDatabase
import com.adeel.youtubeapp.utils.SharedPreferencesHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    private val job = Job()
    private val compositeDisposable = CompositeDisposable()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable>
        get() = _errorLiveData

    @Suppress("UNCHECKED_CAST")
    fun <T> getResponse(
        single: Observable<T>,
        _responseLiveData: MutableLiveData<T>,
        playlistId: String,
        loadingMore: Boolean,
        savedPlaylistIds: String
    ) {
        if (!loadingMore)
            _loadingLiveData.value = true
        compositeDisposable.add(
            single.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<T>() {
                    override fun onComplete() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNext(t: T) {
                        _responseLiveData.value = t

                        if (!loadingMore)
                            _loadingLiveData.value = false

                        if (t is List<*>) {
                            if (t[0] is VideoInfo) {
                                cacheVideos(t as List<VideoInfo>, "$savedPlaylistIds|$playlistId")
                            } else if (t[0] is YoutubePlaylistResponse) {
                                cachePlaylist(t as List<YoutubePlaylistResponse>)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        _errorLiveData.value = e

                        if (!loadingMore)
                            _loadingLiveData.value = false

                    }
                })
        )
    }

    private fun cachePlaylist(playlistLists: List<YoutubePlaylistResponse>?) {
        playlistLists?.let {
            launch {
                val dao = YoutubePlayerDatabase(getApplication()).playlistDao()
//                dao.deleteAllPlaylist()
                dao.insertAll(*it.toTypedArray())
            }
            SharedPreferencesHelper.getInstance(getApplication())
                .savePlaylistUpdateTime(System.nanoTime())
        }
    }

    private fun cacheVideos(videoList: List<VideoInfo>?, playlistId: String) {
        videoList?.let {
            launch {
                val dao = YoutubePlayerDatabase(getApplication()).videoDao()
                dao.deleteAllVideo(playlistId)
                dao.insertAll(*it.toTypedArray())
            }
            SharedPreferencesHelper.getInstance(getApplication())
                .saveVideoUpdateTime("${System.nanoTime()}|$playlistId")
        }
    }

    fun fetchPlaylistFromCache(_youtubePlaylist: MutableLiveData<List<YoutubePlaylistResponse>>) {
        _loadingLiveData.value = true
        launch {
            val playlistList =
                YoutubePlayerDatabase(getApplication()).playlistDao().getAllPlaylist()
            _youtubePlaylist.value = playlistList

            _loadingLiveData.value = false
        }
    }

    fun fetchVideosFromCache(
        playlistId: String,
        _playlistVideos: MutableLiveData<List<VideoInfo>>
    ) {
        _loadingLiveData.value = true
        launch {
            val videosList =
                YoutubePlayerDatabase(getApplication()).videoDao().getAllVideo(playlistId)
            _playlistVideos.value = videosList

            _loadingLiveData.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        job.cancel()
    }
}