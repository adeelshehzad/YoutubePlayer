package com.adeel.youtubeapp.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adeel.youtubeapp.R
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        activityScope.launch {
            delay(1000)

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}
