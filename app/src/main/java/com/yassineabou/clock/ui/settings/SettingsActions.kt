package com.yassineabou.clock.ui.settings

interface SettingsActions {
    fun changeLanguage(language: String)
    fun changeVibration(enabled: Boolean)
    fun changeSignalColor(color: String)
    fun changeSignalIntervalMode(mode: String)
}