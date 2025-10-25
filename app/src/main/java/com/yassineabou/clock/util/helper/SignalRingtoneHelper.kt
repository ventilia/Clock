package com.yassineabou.clock.util.helper

import android.content.Context
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_SOUND_URI
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRingtoneHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
) {

    private val defaultNotificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    fun getSignalRingtoneUri(): Uri {
        val uriString = sharedPreferences.getString(KEY_SIGNAL_SOUND_URI, defaultNotificationUri.toString())
        return Uri.parse(uriString) ?: defaultNotificationUri
    }

    fun setSignalRingtoneUri(uri: Uri?) {
        sharedPreferences.edit().putString(KEY_SIGNAL_SOUND_URI, uri?.toString()).apply()
    }

    fun getSignalRingtoneTitle(uri: Uri? = null): String {
        val actualUri = uri ?: getSignalRingtoneUri()
        val ringtone = RingtoneManager.getRingtone(context, actualUri)
        return ringtone?.getTitle(context) ?: "Unknown"
    }
}