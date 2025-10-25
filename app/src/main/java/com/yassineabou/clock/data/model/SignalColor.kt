package com.yassineabou.clock.data.model

import androidx.compose.ui.graphics.Color

enum class SignalColor(val color: Color) {
    YELLOW(Color(0xFFFFEB3B)),
    RED(Color(0xFFF44336)),
    GREEN(Color(0xFF4CAF50));

    companion object {
        fun fromString(value: String?): SignalColor = value?.let {
            try { valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { YELLOW }
        } ?: YELLOW
    }
}