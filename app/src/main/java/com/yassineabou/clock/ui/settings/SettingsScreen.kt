package com.yassineabou.clock.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yassineabou.clock.R
import com.yassineabou.clock.data.model.SignalColor
import com.yassineabou.clock.data.model.SignalIntervalMode
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_COLOR
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_INTERVAL_MODE
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_SOUND_URI
import com.yassineabou.clock.di.SettingsModule.KEY_VIBRATION_ENABLED
import com.yassineabou.clock.util.components.ClockAppBar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val viewModel: SettingsViewModel = hiltViewModel()
    viewModel.selectedLanguage = prefs.getString("app_language", "en") ?: "en"
    var vibrationEnabled by remember { mutableStateOf(prefs.getBoolean(KEY_VIBRATION_ENABLED, true)) }
    LaunchedEffect(vibrationEnabled) {
        viewModel.changeVibration(vibrationEnabled)
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, vibrationEnabled).apply()
    }
    viewModel.selectedSignalColor = prefs.getString(KEY_SIGNAL_COLOR, SignalColor.YELLOW.name) ?: SignalColor.YELLOW.name
    viewModel.selectedSignalIntervalMode = prefs.getString(KEY_SIGNAL_INTERVAL_MODE, SignalIntervalMode.QUARTER.name) ?: SignalIntervalMode.QUARTER.name
    var expandedLanguage by remember { mutableStateOf(false) }
    var expandedColor by remember { mutableStateOf(false) }
    var expandedMode by remember { mutableStateOf(false) }

    val ringtoneText = stringResource(R.string.ringtone)
    val selectRingtoneText = stringResource(R.string.select_ringtone)

    val signalRingtoneText = stringResource(R.string.signal_sound)
    val selectSignalRingtoneText = stringResource(R.string.select_signal_sound)

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.changeRingtone(uri)
            prefs.edit().putString("alarm_timer_ringtone_uri", uri.toString()).apply()
        }
    }

    val signalRingtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.changeSignalRingtone(uri)
            prefs.edit().putString(KEY_SIGNAL_SOUND_URI, uri.toString()).apply()
        }
    }

    Scaffold(
        topBar = {
            ClockAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(stringResource(R.string.settings)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Блок языка с иконкой
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expandedLanguage,
                onExpandedChange = { expandedLanguage = !expandedLanguage }
            ) {
                TextField(
                    readOnly = true,
                    value = if (viewModel.selectedLanguage == "en") stringResource(R.string.english) else stringResource(R.string.russian),
                    onValueChange = { },
                    label = { Text(stringResource(R.string.language)) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedLanguage,
                    onDismissRequest = { expandedLanguage = false }
                ) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(stringResource(R.string.english)) },
                        onClick = {
                            viewModel.changeLanguage("en")
                            applyLanguage(context, "en", prefs)
                            expandedLanguage = false
                        }
                    )
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(stringResource(R.string.russian)) },
                        onClick = {
                            viewModel.changeLanguage("ru")
                            applyLanguage(context, "ru", prefs)
                            expandedLanguage = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, selectRingtoneText)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, viewModel.selectedRingtoneUri)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            }
                            ringtonePickerLauncher.launch(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = ringtoneText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.selectedRingtoneTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Разделитель с отступами
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))

            // Новый блок: Timer Signal Settings (moved here)
            Text(
                text = stringResource(R.string.timer_signal_settings),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Свитч для вибрации
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Для кликабельности, но switch сам обрабатывает */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.enable_vibration),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Выбор звука сигнала
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, selectSignalRingtoneText)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, viewModel.selectedSignalRingtoneUri)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            }
                            signalRingtonePickerLauncher.launch(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = signalRingtoneText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = viewModel.selectedSignalRingtoneTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Выбор цвета сигнала
            ExposedDropdownMenuBox(
                expanded = expandedColor,
                onExpandedChange = { expandedColor = !expandedColor }
            ) {
                TextField(
                    readOnly = true,
                    value = stringResource(id = getColorStringId(viewModel.selectedSignalColor)),
                    onValueChange = { },
                    label = { Text(stringResource(R.string.signal_color)) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedColor,
                    onDismissRequest = { expandedColor = false }
                ) {
                    SignalColor.values().forEach { color ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(stringResource(id = getColorStringId(color.name))) },
                            onClick = {
                                viewModel.changeSignalColor(color.name)
                                prefs.edit().putString(KEY_SIGNAL_COLOR, color.name).apply()
                                expandedColor = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Выбор интервала сигнала
            ExposedDropdownMenuBox(
                expanded = expandedMode,
                onExpandedChange = { expandedMode = !expandedMode }
            ) {
                TextField(
                    readOnly = true,
                    value = stringResource(id = getModeStringId(viewModel.selectedSignalIntervalMode)),
                    onValueChange = { },
                    label = { Text(stringResource(R.string.signal_interval_mode)) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedMode,
                    onDismissRequest = { expandedMode = false }
                ) {
                    SignalIntervalMode.values().forEach { mode ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(stringResource(id = getModeStringId(mode.name))) },
                            onClick = {
                                viewModel.changeSignalIntervalMode(mode.name)
                                prefs.edit().putString(KEY_SIGNAL_INTERVAL_MODE, mode.name).apply()
                                expandedMode = false
                            }
                        )
                    }
                }
            }

            // Разделитель с отступами
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))

            // Блок GitHub с иконкой, заголовком и описанием
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yassineAbou/Clock"))
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "GitHub",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.original_repo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Разделитель с отступами для следующего блока
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(16.dp))

            // Новый блок modified_by с иконкой и кликабельностью
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ventilia"))
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.modified_by),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

private fun getColorStringId(name: String): Int {
    return when (name.uppercase()) {
        "YELLOW" -> R.string.yellow
        "RED" -> R.string.red
        "GREEN" -> R.string.green
        else -> R.string.yellow
    }
}

private fun getModeStringId(name: String): Int {
    return when (name.uppercase()) {
        "HALF" -> R.string.half
        "QUARTER" -> R.string.quarter
        "THIRD" -> R.string.third
        "EIGHTH" -> R.string.eighth
        "TENTH" -> R.string.tenth
        else -> R.string.quarter
    }
}

private fun applyLanguage(context: Context, language: String, prefs: SharedPreferences) {
    prefs.edit().putString("app_language", language).commit()

    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    (context as? ComponentActivity)?.recreate()
}