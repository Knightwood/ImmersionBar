package android.accompanist.dialoghelper.dialog

import android.accompanist.dialoghelper.R
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.ComponentDialog
import androidx.activity.addCallback
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object DialogFullScreenHelper {

    fun setUp(dialog: Dialog) {
        dialog.run {
            setWindowFlags()
        }
    }

    /**
     * 功能与[setUp]一致，还不需使用特定style。
     *
     * tip:
     * 如果需要背景有个暗色遮罩，可以在主题中启用
     * ```
     * <item name="android:backgroundDimEnabled">true</item>
     * ```
     *
     * 或者使用代码设置暗色遮罩
     * [android.accompanist.dialoghelper.utils.backgroundDim]
     *
     */
    fun setUp2(dialog: Dialog) {
        dialog.run {
            window?.let { window ->
                //使用此方法可以解决大部分问题，还不用在style中添加繁多的属性
                WindowCompat.enableEdgeToEdge(window)
                if (window.isFloating) {
                    //1. 如果将windowIsFloating设置为true，则内容视图会自动调整为WRAP_CONTENT,将不会填充屏幕
                    //因此，需要重新设置为MATCH_PARENT以使根布局撑满屏幕
                    //2. 将Window布局设置为MATCH_PARENT，setCanceledOnTouchOutside将失效
                    //现在默认就是MATCH_PARENT的，如果需要点击外部区域消失，需要设置为WRAP_CONTENT
                    window.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )
                }

                //style中设置padding无效，只能这里设置
                window.decorView.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun Dialog.setWindowFlags() {
        context.setTheme(R.style.BaseFullscreenDialog)
        val window = window ?: return
        //此标志会导致真全屏，状态栏隐藏，如果再次显示状态栏会导致布局位置跳变
        //没有此标志依旧可以沉浸，因此需要在此去除此标志，防止主题中设置此标志
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // 设置全屏延展标志
        window.addFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
        )
        // 设置背景
        window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootView = window.decorView.findViewById<View>(android.R.id.content)

        val controller = WindowCompat.getInsetsController(window, rootView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        //windowFullScreen会使状态栏隐藏，所以这里需要调用show方法显示状态栏
        controller.show(WindowInsetsCompat.Type.systemBars())

        if (window.isFloating) {
            //1. 如果将windowIsFloating设置为true，则内容视图会自动调整为WRAP_CONTENT,将不会填充屏幕
            //因此，需要重新设置为MATCH_PARENT以使根布局撑满屏幕
            //2. 将Window布局设置为MATCH_PARENT，setCanceledOnTouchOutside将失效
            //现在默认就是MATCH_PARENT的，如果需要点击外部区域消失，需要设置为WRAP_CONTENT
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

    fun Dialog.keepInsets() {
        val contentView = findViewById<View>(android.R.id.content)
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

    fun ComponentDialog.enableOnBackPassedDismiss(onDismissRequest: () -> Unit) {
        // Due to how the onDismissRequest callback works
        // (it enforces a just-in-time decision on whether to update the state to hide the dialog)
        // we need to unconditionally add a callback here that is always enabled,
        // meaning we'll never get a system UI controlled predictive back animation
        // for these dialogs
        //支持返回键关闭弹窗
        onBackPressedDispatcher.addCallback(this) {
            onDismissRequest()
        }
    }
}
