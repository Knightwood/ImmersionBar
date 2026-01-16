package com.kiylx.customviews.dialog.utils

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlin.math.roundToInt

object ViewHelpers {
    /**
     * 判断点击事件是否在ViewGroup的子View内
     */
    fun isInsideContent(group: ViewGroup, event: MotionEvent): Boolean {
        if (!event.x.isFinite() || !event.y.isFinite()) return false
        val child = group.getChildAt(0) ?: return false
        val left = group.left + child.left
        val right = left + child.width
        val top = group.top + child.top
        val bottom = top + child.height
        return event.x.roundToInt() in left..right && event.y.roundToInt() in top..bottom
    }

    /**
     * 判断是否开启了SecureFlag
     */
    fun isFlagSecureEnabled(view: View): Boolean {
        val windowParams = view.rootView.layoutParams as? WindowManager.LayoutParams
        if (windowParams != null) {
            return (windowParams.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
        }
        return false
    }
}
