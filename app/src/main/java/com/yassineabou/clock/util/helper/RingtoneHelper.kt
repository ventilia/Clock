package com.yassineabou.clock.util.helper

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RingtoneHelper @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val PREF_RINGTONE_URI = "ringtone_uri"
    }

    fun getRingtoneUri(): Uri {
        val uriString = sharedPreferences.getString(PREF_RINGTONE_URI, null)
        return if (uriString != null) {
            Uri.parse(uriString)
        } else {
            Settings.System.DEFAULT_ALARM_ALERT_URI ?: Uri.parse("android.resource://${applicationContext.packageName}/raw/alarm")
        }
    }

    fun setRingtoneUri(uri: Uri?) {
        sharedPreferences.edit().apply {
            putString(PREF_RINGTONE_URI, uri?.toString())
            apply()
        }
    }

    fun getRingtoneTitle(uri: Uri): String {
        return RingtoneManager.getRingtone(applicationContext, uri)?.getTitle(applicationContext) ?: "Default Alarm"
    }
}