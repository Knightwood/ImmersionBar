package android.accompanist.dialoghelper.component

import android.R
import android.accompanist.dialoghelper.utils.DialogFullScreenHelper
import android.accompanist.dialoghelper.utils.ViewHelpers
import android.accompanist.dialoghelper.utils.isMatchParent
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.compose.runtime.Composable

private const val TAG = "FullScreenDialog"
internal typealias ComposeUI = @Composable () -> Unit

/**
 * 使用此Dialog,不要传入自定义主题
 */
open class FullScreenDialog(context: Context) : ComponentDialog(context) {
    /**
     * Dialog内部的mCancelable默认值就是true
     * 所以这里也默认为true
     */
    private var cancelable = true

    init {
        DialogFullScreenHelper.setUp(this)
    }

    /**
     *在Window使用了MATCH_PARENT时失效
     *因为所有触摸都发生在弹窗Window区域内，无法触发外部触摸取消机制
     *但是，我们实现了当Window使用了MATCH_PARENT时，
     *通过判断点击事件是否发生在FullScreenDialog.getCancelableView()布局中，
     *实现了点击外部区域取消弹窗功能
     */
    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        cancelable = cancel
    }

    private var isPressOutside = false

    /**
     * 获取R.id.content
     * 或者
     * setContentView设置的跟布局
     */
    private fun getCancelableView(): ViewGroup? {
        val content = window?.decorView?.findViewById<FrameLayout>(R.id.content)
        if (content == null) {
            Log.d(TAG, "content is null")
            return null
        }
        val root: View? = content.getChildAt(0)
        if (root is ViewGroup) {
            if (root.isMatchParent()) {
                Log.d(TAG, "root is match parent")
                return root
            } else {
                Log.d(TAG, "root is not match parent")
                if (content.isMatchParent()) {
                    Log.d(TAG, "content is match parent")
                    return content
                } else {
                    return window?.decorView as ViewGroup
                }
            }
        } else {
            Log.d(TAG, "root is not ViewGroup")
            return content
        }
    }

    /**
     * 核心功能抄自Compose中的BasicAlertDialog
     * 获取哪个响应事件的布局是自己写的
     *
     * 重新onTouchEvent的目的：
     *
     * 当我们的Window设置为MATCH_PARENT
     * （window.isFloating时Window应该为WRAP_CONTENT，但是我们强制使Window使用了MATCH_PARENT），
     * 根本没有外部区域，setCanceledOnTouchOutside自然就失效了
     * 所以在这种情况下，我们需要对触摸事件进行拦截，
     * 1. 如果用户调用setContentView设置的根布局使用了MATCH_PARENT
     *    我们就判断点击事件是否发生在根布局，是的话调用cancel
     * 2. 如果用户调用setContentView设置的根布局使用了WRAP_CONTENT，
     *    我们就判断一下点击事件是否发生在android.R.id.content布局中，是的话调用cancel
     *
     *
     *只有window设置了MATCH_PARENT时，才会失去外部区域，无法响应点击外部cancel功能
     *所以我们在这里做了判断，如果window设置了MATCH_PARENT，且设置了可以cancel
     *我们就通过判断点击事件是否发生在getCancelableView()布局中实现点击外部区域cancel功能
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = super.onTouchEvent(event)

        val windowFullscreen = window?.attributes?.isMatchParent()
        if (windowFullscreen == true && cancelable) {
            val isInside = ViewHelpers.isInside(getCancelableView(), event)
            if (isInside) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        isPressOutside = true
                        result = true
                    }

                    MotionEvent.ACTION_UP ->
                        if (isPressOutside) {
                            cancel()
                            result = true
                            isPressOutside = false
                        }

                    MotionEvent.ACTION_CANCEL -> isPressOutside = false
                }
            }
        } else {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL,
                    -> isPressOutside = false
            }
        }
        return result
    }

    /**
     * 功能抄自Compose中的BasicAlertDialog
     * 实现按下esc键cancel弹窗
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (
            cancelable &&
            event.isTracking &&
            !event.isCanceled &&
            keyCode == KeyEvent.KEYCODE_ESCAPE
        ) {
            cancel()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }
}
