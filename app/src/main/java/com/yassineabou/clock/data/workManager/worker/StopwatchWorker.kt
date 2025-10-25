package com.yassineabou.clock.data.workManager.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.yassineabou.clock.data.manager.StopwatchManager
import com.yassineabou.clock.util.helper.STOPWATCH_WORKER_NOTIFICATION_ID
import com.yassineabou.clock.util.helper.StopwatchNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest

@HiltWorker
class StopwatchWorker @AssistedInject constructor(
    @Assisted private val stopwatchManager: StopwatchManager,
    @Assisted private val stopwatchNotificationHelper: StopwatchNotificationHelper,
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result {
        return try {
            val foregroundInfo = ForegroundInfo(
                STOPWATCH_WORKER_NOTIFICATION_ID,
                stopwatchNotificationHelper.getStopwatchBaseNotification().build(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
            )

            try {
                setForeground(foregroundInfo)
            } catch (e: android.app.ForegroundServiceStartNotAllowedException) {
                return Result.failure()
            }

            var previousSecond = ""

            stopwatchManager.stopwatchState.collectLatest {
                if (!it.isReset) {
                    val msDisplay = if (it.isPlaying) "00" else it.ms
                    val currentTime = "${it.hour}:${it.minute}:${it.second}:${msDisplay}"
                    if (it.second != previousSecond) {
                        stopwatchNotificationHelper.updateStopwatchWorkerNotification(
                            time = currentTime,
                            isPlaying = it.isPlaying,
                            lastLapIndex = stopwatchManager.lapTimes.lastIndex,
                        )
                        previousSecond = it.second
                    }
                }
            }

            Result.success()
        } catch (e: CancellationException) {
            stopwatchNotificationHelper.removeStopwatchNotification()
            Result.failure()
        }
    }
}

const val STOPWATCH_TAG = "stopwatchTag"