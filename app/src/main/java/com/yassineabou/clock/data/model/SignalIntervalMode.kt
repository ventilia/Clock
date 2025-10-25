package com.yassineabou.clock.data.model

enum class SignalIntervalMode {
    HALF {
        override fun getThresholds(): List<Float> = listOf(0.5f)
    },
    QUARTER {
        override fun getThresholds(): List<Float> = listOf(0.75f, 0.5f, 0.25f)
    },
    THIRD {
        override fun getThresholds(): List<Float> = listOf(2f/3f, 1f/3f)
    },
    EIGHTH {
        override fun getThresholds(): List<Float> = (1..7).map { it.toFloat() / 8f }.reversed()
    },
    TENTH {
        override fun getThresholds(): List<Float> = (1..9).map { it.toFloat() / 10f }.reversed()
    };

    abstract fun getThresholds(): List<Float>

    companion object {
        fun fromString(value: String?): SignalIntervalMode = value?.let {
            try { valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { QUARTER }
        } ?: QUARTER
    }
}