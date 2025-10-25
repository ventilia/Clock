package com.yassineabou.clock.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.yassineabou.clock.util.helper.RingtoneHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val ringtoneHelper: RingtoneHelper
) : ViewModel(), SettingsActions {

    var selectedLanguage: String = "en"

    var selectedRingtoneUri: Uri = ringtoneHelper.getRingtoneUri()

    var selectedRingtoneTitle: String = ringtoneHelper.getRingtoneTitle(selectedRingtoneUri)

    override fun changeLanguage(language: String) {
        selectedLanguage = language
    }

    fun changeRingtone(uri: Uri?) {
        ringtoneHelper.setRingtoneUri(uri)
        selectedRingtoneUri = ringtoneHelper.getRingtoneUri()
        selectedRingtoneTitle = ringtoneHelper.getRingtoneTitle(selectedRingtoneUri)
    }
}