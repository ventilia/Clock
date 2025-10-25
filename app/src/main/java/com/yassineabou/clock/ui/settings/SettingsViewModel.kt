package com.yassineabou.clock.ui.settings

import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.yassineabou.clock.data.model.SignalColor
import com.yassineabou.clock.data.model.SignalIntervalMode
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_COLOR
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_INTERVAL_MODE
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_SOUND_URI
import com.yassineabou.clock.di.SettingsModule.KEY_VIBRATION_ENABLED
import com.yassineabou.clock.util.helper.RingtoneHelper
import com.yassineabou.clock.util.helper.SignalRingtoneHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val ringtoneHelper: RingtoneHelper,
    val signalRingtoneHelper: SignalRingtoneHelper,
) : ViewModel(), SettingsActions {

    var selectedLanguage: String = "en"

    var selectedRingtoneUri: Uri = ringtoneHelper.getRingtoneUri()

    var selectedRingtoneTitle: String = ringtoneHelper.getRingtoneTitle(selectedRingtoneUri)

    var vibrationEnabled: Boolean = true

    var selectedSignalColor: String = SignalColor.YELLOW.name

    var selectedSignalIntervalMode: String = SignalIntervalMode.QUARTER.name

    var selectedSignalRingtoneUri: Uri = signalRingtoneHelper.getSignalRingtoneUri()

    var selectedSignalRingtoneTitle: String = signalRingtoneHelper.getSignalRingtoneTitle()

    override fun changeLanguage(language: String) {
        selectedLanguage = language
    }

    fun changeRingtone(uri: Uri?) {
        ringtoneHelper.setRingtoneUri(uri)
        selectedRingtoneUri = ringtoneHelper.getRingtoneUri()
        selectedRingtoneTitle = ringtoneHelper.getRingtoneTitle(selectedRingtoneUri)
    }

    override fun changeVibration(enabled: Boolean) {
        vibrationEnabled = enabled
    }

    override fun changeSignalColor(color: String) {
        selectedSignalColor = color
    }

    override fun changeSignalIntervalMode(mode: String) {
        selectedSignalIntervalMode = mode
    }

    fun changeSignalRingtone(uri: Uri?) {
        signalRingtoneHelper.setSignalRingtoneUri(uri)
        selectedSignalRingtoneUri = signalRingtoneHelper.getSignalRingtoneUri()
        selectedSignalRingtoneTitle = signalRingtoneHelper.getSignalRingtoneTitle()
    }
}