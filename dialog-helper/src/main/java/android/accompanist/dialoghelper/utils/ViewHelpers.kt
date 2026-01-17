package android.accompanist.dialoghelper.utils

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import kotlin.math.roundToInt

object ViewHelpers {
    /**
     * 判断点击事件是否在View内
     */
    fun isInside(view: View?, event: MotionEvent): Boolean {
        if (view == null) return false
        if (!event.x.isFinite() || !event.y.isFinite()) return false
        val left = view.left
        val right = view.right
        val top = view.top
        val bottom = view.bottom
        Log.d("isInside", "left:$left,right:$right,top:$top,bottom:$bottom")
        Log.d("isInside", "event.x:${event.x},event.y:${event.y}")
        return event.x.roundToInt() in left..right && event.y.roundToInt() in top..bottom
    }

    /**
     * 判断点击事件是否在ViewGroup的子View内
     */
    fun isInsideContent(group: ViewGroup?, event: MotionEvent): Boolean {
        if (group == null) return false
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

object WindowSecureFlagPolicy {
    const val NONE = 0

    //启用安全标志，禁止截屏
    const val SECURE_FLAG_ENABLE = WindowManager.LayoutParams.FLAG_SECURE
    const val SECURE_FLAG_DISABLE = WindowManager.LayoutParams.FLAG_SECURE.inv()
}

fun Window.setSecureFlag(flag: Int = WindowSecureFlagPolicy.NONE) {
    if (flag != WindowSecureFlagPolicy.NONE) {
        setFlags(flag, WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

fun View.isMatchParent(): Boolean {
    return layoutParams.isMatchParent()
}

fun ViewGroup.LayoutParams.isMatchParent(): Boolean {
    return width == ViewGroup.LayoutParams.MATCH_PARENT &&
            height == ViewGroup.LayoutParams.MATCH_PARENT
}

fun View.isWrapContent(): Boolean {
    return layoutParams.isWrapContent()
}

fun ViewGroup.LayoutParams.isWrapContent(): Boolean {
    return width == ViewGroup.LayoutParams.WRAP_CONTENT &&
            height == ViewGroup.LayoutParams.WRAP_CONTENT
}

/**
 *@param dim  0-1 之间 如果设置为1 就是全黑色了
 */
fun Window.backgroundDim(dim: Float = 0.5f) {
    if (dim != 0f) {
        setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    } else {
        clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    setDimAmount(dim)
}
