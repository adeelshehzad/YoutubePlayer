package com.adeel.youtubeapp.view.fragment

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.adeel.youtubeapp.R
import com.adeel.youtubeapp.utils.*
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import kotlinx.android.synthetic.main.fragment_login.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private val googleCredential by lazy {
        GoogleAccountCredential.usingOAuth2(
            activity!!,
            listOf(YouTubeScopes.YOUTUBE_READONLY)
        ).setBackOff(ExponentialBackOff())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnGoogleSignin.setOnClickListener {
            attemptGoogleSignin()
        }
    }

    private fun attemptGoogleSignin() {
        val accountName = SharedPreferencesHelper.getInstance(activity!!).accountName

        if (!isDeviceOnline(activity!!)) {
            Toast.makeText(activity!!, "Not connected to Internet", Toast.LENGTH_LONG).show()
        } else if (!isGooglePlayServicesAvailable(activity!!)) {
            return
        } else if (!TextUtils.isEmpty(accountName)) {
            googleCredential.selectedAccountName = accountName
            loadHomeFragment()
        } else {
            chooseAccount()
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    fun chooseAccount() {
        if (EasyPermissions.hasPermissions(activity!!, Manifest.permission.GET_ACCOUNTS)) {
            startActivityForResult(
                googleCredential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account for YouTube.",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> {
                if (resultCode == RESULT_OK) {
                    attemptGoogleSignin()
                }
            }

            REQUEST_ACCOUNT_PICKER -> {
                data?.let { intentData ->
                    if (resultCode == RESULT_OK && intentData.extras != null) {
                        val accountName = intentData.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                        googleCredential.selectedAccountName = accountName

                        loadHomeFragment()
                    }
                }
            }
        }
    }

    fun loadHomeFragment() {
        CacheManager.put(KEY_GOOGLE_ACCOUNT, googleCredential)
        SharedPreferencesHelper.getInstance(activity!!)
            .saveAccountName(googleCredential.selectedAccountName)
        navController.navigate(R.id.actionLoadHomeFragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this
        )

    }
}
