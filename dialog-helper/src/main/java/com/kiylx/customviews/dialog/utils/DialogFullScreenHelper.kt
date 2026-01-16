package com.kiylx.customviews.dialog.utils

import android.R
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

object DialogFullScreenHelper {

    fun setUp(dialog: Dialog) {
        dialog.run {
            setWindowFlags()
        }
    }

    private fun Dialog.setWindowFlags() {
        context.setTheme(android.accompanist.dialoghelper.R.style.BaseFullscreenDialog)
        val window = window ?: return
        // 设置全屏延展标志
        window.addFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
        )
        // 设置背景
        window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setOnShowListener {
            val rootView = window.decorView.findViewById<View>(R.id.content)

            val controller = WindowCompat.getInsetsController(window, rootView)
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
            //windowFullScreen会使状态栏隐藏，所以这里需要调用show方法显示状态栏
            controller.show(WindowInsetsCompat.Type.systemBars())

            if (window.isFloating) {
                //如果将windowIsFloating设置为true，则内容视图会自动调整为WRAP_CONTENT,将不会填充屏幕
                //因此，需要重新设置为MATCH_PARENT以使根布局撑满屏幕
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            }
            //style中设置padding无效，只能这里设置
            window.decorView.setPadding(0, 0, 0, 0)

            fun ViewGroup.disableClipping() {
                clipChildren = false
                for (i in 0 until childCount) {
                    (getChildAt(i) as? ViewGroup)?.disableClipping()
                }
            }

            // Turn of all clipping so shadows can be drawn outside the window
            (window.decorView as? ViewGroup)?.disableClipping()
        }
    }

    fun Dialog.keepInsets() {
        val contentView = findViewById<View>(R.id.content)
        contentView?.let { view ->
            // 设置根视图的 padding 来处理系统栏
            view.setOnApplyWindowInsetsListener { v, insets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // 仍然为系统栏留出空间，而不是覆盖它们
                    v.setPadding(
                        insets.getInsets(WindowInsets.Type.systemBars()).left,
                        insets.getInsets(WindowInsets.Type.systemBars()).top,
                        insets.getInsets(WindowInsets.Type.systemBars()).right,
                        insets.getInsets(WindowInsets.Type.systemBars()).bottom
                    )
                } else {
                    @Suppress("DEPRECATION")
                    v.setPadding(
                        insets.systemWindowInsetLeft,
                        insets.systemWindowInsetTop,
                        insets.systemWindowInsetRight,
                        insets.systemWindowInsetBottom
                    )
                }
                insets
            }

            // 请求应用窗口插入
            view.requestApplyInsets()
        }
    }
}
