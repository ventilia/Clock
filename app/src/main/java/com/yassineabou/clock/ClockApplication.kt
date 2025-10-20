package com.yassineabou.clock

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.work.Configuration
import com.yassineabou.clock.data.workManager.factory.WrapperWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class ClockApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WrapperWorkerFactory

    override fun onCreate() {
        super.onCreate()

    }

    override fun attachBaseContext(base: Context) {
        val prefs = base.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val language = prefs.getString("app_language", "en") ?: "en"
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = android.content.res.Configuration(base.resources.configuration)
        config.setLocale(locale)
        val context = base.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}