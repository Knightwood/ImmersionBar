package com.kiylx.libx.windowinsinsets

import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentActivity
import androidx.palette.graphics.Palette


// <editor-fold desc="Palette取色">
/**
 * 传入bitmap，通过palette取色，设置状态栏的主题
 */
fun FragmentActivity.detectBitmapColor(bitmap: Bitmap) {
    val colorCount = 5
    val left = 0
    val top = 0
    val right = getScreenWidth()
    val bottom = getSystemBarInsetsIgnoringVisibility().top

    Palette
        .from(bitmap)
        .maximumColorCount(colorCount)
        .setRegion(left, top, right, bottom)
        .generate {
            it?.let { palette ->
                var mostPopularSwatch: Palette.Swatch? = null
                for (swatch in palette.swatches) {
                    if (mostPopularSwatch == null
                        || swatch.population > mostPopularSwatch.population
                    ) {
                        mostPopularSwatch = swatch
                    }
                }
                mostPopularSwatch?.let { swatch ->
                    val luminance = ColorUtils.calculateLuminance(swatch.rgb)
                    // If the luminance value is lower than 0.5, we consider it as dark.
                    if (luminance < 0.5) {
                        setDarkStatusBar()
                    } else {
                        setLightStatusBar()
                    }
                }
            }
        }
}

//</editor-fold>