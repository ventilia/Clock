package com.yassineabou.clock.util.helper

import android.content.Context
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_SOUND_URI
import com.yassineabou.clock.di.SettingsModule.KEY_VIBRATION_ENABLED
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerSignalHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
) {

    private val defaultNotificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun triggerSignal() {
        // Play sound
        val soundUriString = sharedPreferences.getString(KEY_SIGNAL_SOUND_URI, defaultNotificationUri.toString())
        val soundUri = Uri.parse(soundUriString)
        val ringtone: Ringtone? = RingtoneManager.getRingtone(context, soundUri)
        ringtone?.play()

        // Trigger vibration if enabled
        val vibrationEnabled = sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
        if (vibrationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        }
    }
}