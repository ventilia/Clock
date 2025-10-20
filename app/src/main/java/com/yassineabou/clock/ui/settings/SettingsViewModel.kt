package com.yassineabou.clock.ui.settings

import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import java.util.Locale

class SettingsViewModel : ViewModel(), SettingsActions {

    var selectedLanguage: String = "en"

    override fun changeLanguage(language: String) {
        selectedLanguage = language

    }
}