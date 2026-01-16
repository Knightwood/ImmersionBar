package com.kiylx.libx.windowinsinsets

import android.graphics.Color
import androidx.annotation.ColorInt

object ColorUtils {
    fun isColorDark(color: Int): Boolean {
        return (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255 < 0.5
    }

    @ColorInt
    fun darkColor(@ColorInt color: Int): Int {
        return Color.argb(
            255,
            addToColorPart(Color.red(color), -20),
            addToColorPart(Color.green(color), -20),
            addToColorPart(Color.blue(color), -20)
        )
    }

    @ColorInt
    fun lightColor(@ColorInt color: Int): Int {
        return Color.argb(
            255,
            addToColorPart(Color.red(color), 20),
            addToColorPart(Color.green(color), 20),
            addToColorPart(Color.blue(color), 20)
        )
    }

    private fun addToColorPart(colorPart: Int, variable: Int): Int {
        return Math.max(0, Math.min(255, colorPart + variable))
    }

    fun muteColor(color: Int, variant: Int): Int {
        val mutedColor = Color.argb(
            255,
            (127.5 + Color.red(color)).toInt() / 2,
            (127.5 + Color.green(color)).toInt() / 2,
            (127.5 + Color.blue(color)).toInt() / 2
        )
        return when (variant % 3) {
            1 -> Color.argb(
                255,
                Color.red(mutedColor) + 10,
                Color.green(mutedColor) + 10,
                Color.blue(mutedColor) + 10
            )
            2 -> Color.argb(
                255,
                Color.red(mutedColor) - 10,
                Color.green(mutedColor) - 10,
                Color.blue(mutedColor) - 10
            )
            else -> mutedColor
        }
    }
}