package com.yassineabou.clock.util.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yassineabou.clock.R
import com.yassineabou.clock.ui.theme.Red100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerCompletedDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onRestart: () -> Unit
) {
    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .widthIn(min = 320.dp)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.timer_completed),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ClockButton(
                            text = stringResource(id = R.string.dismiss),
                            onClick = onDismiss,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ClockButton(
                            text = stringResource(id = R.string.restart),
                            onClick = onRestart
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCompletedDialog(
    showDialog: Boolean,
    alarmTitle: String,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .widthIn(min = 320.dp)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.alarm_ringing),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = alarmTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Column( // Изменено: Row заменён на Column
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ClockButton(
                            text = stringResource(id = R.string.dismiss),
                            onClick = onDismiss,
                            color = Red100
                        )
                        ClockButton(
                            text = stringResource(id = R.string.snooze),
                            onClick = onSnooze
                        )
                    }
                }
            }
        }
    }
}