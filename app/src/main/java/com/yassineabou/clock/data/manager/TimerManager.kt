package com.yassineabou.clock.data.manager

import android.content.SharedPreferences
import com.yassineabou.clock.data.model.SignalColor
import com.yassineabou.clock.data.model.SignalIntervalMode
import com.yassineabou.clock.data.model.TimerState
import com.yassineabou.clock.data.workManager.worker.TIMER_COMPLETED_TAG
import com.yassineabou.clock.data.workManager.worker.TIMER_RUNNING_TAG
import com.yassineabou.clock.data.workManager.worker.TimerCompletedWorker
import com.yassineabou.clock.data.workManager.worker.TimerRunningWorker
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_COLOR
import com.yassineabou.clock.di.SettingsModule.KEY_SIGNAL_INTERVAL_MODE
import com.yassineabou.clock.util.GlobalProperties.TIME_FORMAT
import com.yassineabou.clock.util.helper.CountDownTimerHelper
import com.yassineabou.clock.util.helper.TimerSignalHelper
import com.zhuinden.flowcombinetuplekt.combineTuple
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Singleton
class TimerManager @Inject constructor(
    private val workRequestManager: WorkRequestManager,
    private val timerSignalHelper: TimerSignalHelper,
    private val sharedPreferences: SharedPreferences,
) {

    private val timeInMillisFlow = MutableStateFlow(0L)
    private val timeTextFlow = MutableStateFlow("00:00:00")
    private val hourFlow = MutableStateFlow(0)
    private val minuteFlow = MutableStateFlow(0)
    private val secondFlow = MutableStateFlow(0)
    private val progressFlow = MutableStateFlow(0f)
    private val isPlayingFlow = MutableStateFlow(false)
    private val isDoneFlow = MutableStateFlow(true)
    private val signalTriggerFlow = MutableStateFlow(0)
    private val signalColorFlow = MutableStateFlow(SignalColor.YELLOW.color)
    private val isCompletedFlow = MutableStateFlow(false)

    private var thresholds: List<Float> = SignalIntervalMode.QUARTER.getThresholds()
    private val signaledQuarters = mutableSetOf<Float>()
    private var initialTimeInMillis: Long = 0L

    val timerState = combineTuple(
        timeInMillisFlow,
        timeTextFlow,
        hourFlow,
        minuteFlow,
        secondFlow,
        progressFlow,
        isPlayingFlow,
        isDoneFlow,
        signalTriggerFlow,
        signalColorFlow,
        isCompletedFlow
    ).map { (timeInMillis, time, hour, minute, second, progress, isPlaying, isDone, signalTrigger, signalColor, isCompleted) ->
        TimerState(
            timeInMillis = timeInMillis,
            hour = hour,
            minute = minute,
            second = second,
            timeText = time,
            progress = progress,
            isPlaying = isPlaying,
            isDone = isDone,
            signalTrigger = signalTrigger,
            signalColor = signalColor,
            isCompleted = isCompleted
        )
    }

    private var countDownTimerHelper: CountDownTimerHelper? = null

    fun setTHour(hour: Int) {
        hourFlow.value = hour
    }

    fun setMinute(minute: Int) {
        minuteFlow.value = minute
    }

    fun setSecond(second: Int) {
        secondFlow.value = second
    }

    fun setCountDownTimer() {
        timeInMillisFlow.value =
            (hourFlow.value.hours + minuteFlow.value.minutes + secondFlow.value.seconds).inWholeMilliseconds
        initialTimeInMillis = timeInMillisFlow.value
        countDownTimerHelper = object : CountDownTimerHelper(timeInMillisFlow.value, 1000) {
            override fun onTimerTick(millisUntilFinished: Long) {
                val progressValue = if (initialTimeInMillis > 0) {
                    millisUntilFinished.toFloat() / initialTimeInMillis
                } else {
                    0f
                }
                handleTimerValues(true, millisUntilFinished.formatTime(), progressValue)
                for (threshold in thresholds) {
                    if (progressValue <= threshold && !signaledQuarters.contains(threshold)) {
                        timerSignalHelper.triggerSignal()
                        signalTriggerFlow.value++
                        signaledQuarters.add(threshold)
                    }
                }
            }
            override fun onTimerFinish() {
                workRequestManager.enqueueWorker<TimerCompletedWorker>(TIMER_COMPLETED_TAG)
                reset()
                isCompletedFlow.value = true
            }
        }
    }

    fun pause() {
        countDownTimerHelper?.pause()
        isPlayingFlow.value = false
    }

    fun reset() {
        countDownTimerHelper?.restart()
        handleTimerValues(false, timeInMillisFlow.value.formatTime(), 0f)
        isDoneFlow.value = true
        workRequestManager.cancelWorker(TIMER_RUNNING_TAG)
        signaledQuarters.clear()
        signalTriggerFlow.value = 0
        loadSettings()
    }

    fun start() {
        countDownTimerHelper?.start()
        isPlayingFlow.value = true
        if (isDoneFlow.value) {
            progressFlow.value = 1f
            workRequestManager.enqueueWorker<TimerRunningWorker>(TIMER_RUNNING_TAG)
            isDoneFlow.value = false
            loadSettings()
        }
    }


    fun dismissTimer() {
        countDownTimerHelper?.restart()
        workRequestManager.cancelWorker(TIMER_COMPLETED_TAG)
        isCompletedFlow.value = false
    }


    fun restartTimer() {
        countDownTimerHelper?.restart()
        workRequestManager.cancelWorker(TIMER_COMPLETED_TAG)
        timeInMillisFlow.value = initialTimeInMillis
        isCompletedFlow.value = false
        signaledQuarters.clear()
        signalTriggerFlow.value = 0
        start()
    }

    private fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
    ) {
        isPlayingFlow.value = isPlaying
        timeTextFlow.value = text
        progressFlow.value = progress
    }

    private fun loadSettings() {
        val intervalModeStr = sharedPreferences.getString(KEY_SIGNAL_INTERVAL_MODE, "QUARTER")
        thresholds = SignalIntervalMode.fromString(intervalModeStr).getThresholds()

        val colorStr = sharedPreferences.getString(KEY_SIGNAL_COLOR, "YELLOW")
        signalColorFlow.value = SignalColor.fromString(colorStr).color
    }

    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60,
    )
}