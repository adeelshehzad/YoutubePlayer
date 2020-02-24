package com.adeel.youtubeapp.utils

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun isGooglePlayServicesAvailable(activity: Activity): Boolean {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)

    if (connectionStatusCode != ConnectionResult.SUCCESS) {
        acquireGooglePlayServices(activity)
        return false
    }
    return true
}

private fun acquireGooglePlayServices(activity: Activity) {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity)
    if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
        showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode)
    }
}

private fun showGooglePlayServicesAvailabilityErrorDialog(
    activity: Activity,
    connectionStatusCode: Int
) {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val dialog = apiAvailability.getErrorDialog(
        activity,
        connectionStatusCode,
        REQUEST_GOOGLE_PLAY_SERVICES
    )
    dialog.show()
}