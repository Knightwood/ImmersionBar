package android.accompanist.dialoghelper.dialog_fragment

import android.accompanist.dialoghelper.R
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment

/**
 * Full screen dialog fragment1
 *
 * 这里的全屏、状态来就沉浸处理分为了两部分：
 * 1. 使用setStyle(STYLE_NORMAL, R.style.BaseFullscreenDialog2)设置弹窗主题
 * 2. 在onStart阶段对弹窗窗口进行全屏、状态来就沉浸处理
 * DialogFragment使用onCreateDialog返回的弹窗实例显示onCreateView返回的布局，
 * 所以，全屏、状态栏沉浸处理也可以放到onCreateDialog返回的Dialog中，请查看：[FullScreenDialogFragment2]
 *
 * @constructor Creates a new FullScreenDialogFragment1
 */
open class FullScreenDialogFragment1: DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DialogFragment 的 Dialog 主题是在 onCreate → onCreateDialog/onGetLayoutInflater 阶段确定的。
        // 在 onCreateView 里调 setStyle，Dialog 的 window 已经用默认主题（Theme.Material3.DayNight.Dialog，默认 windowIsFloating=true）创建完了，
        // windowIsFloating=false、windowBackground=transparent 统统没生效。
        // 必须在 super.onCreate 之前或至少 onCreate 内调用
        setStyle(STYLE_NORMAL, R.style.BaseFullscreenDialog2)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            // 1. 打开 edge-to-edge（内部会 setDecorFitsSystemWindows(false) 并把系统栏设为透明）
            //    Android 15+ 已强制 edge-to-edge，这里对老系统同样有效；注意它会把
            //    isNavigationBarContrastEnforced 设回 true，所以下面对比度设置必须放在它之后
            WindowCompat.enableEdgeToEdge(window)
            // 2. 让 Dialog 窗口铺满整个屏幕（含状态栏、导航栏区域）
//            window.decorView.setPadding(0, 0, 0, 0)
//            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            // 3. 关闭导航栏“对比度保护”，否则三键导航底部会叠一层 80% 的黑
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                @Suppress("DEPRECATION")
                window.isNavigationBarContrastEnforced = false
            }

            // 4. 窗口背景保持透明，由根布局的 ?android:attr/colorBackground 负责铺满
            window.setBackgroundDrawableResource(R.color.transparent)

            // 5. 系统栏图标颜色：根据背景色自动调整为浅色或深色
            val isLight =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES
            WindowInsetsControllerCompat(window, window.decorView).apply {
                // 背景是浅色，系统栏图标用深色
                isAppearanceLightStatusBars = isLight
                isAppearanceLightNavigationBars = isLight
            }
            // 6. 监听系统栏 insets 变化并更新内容区域 padding
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView.findViewById(android.R.id.content)) { v, insets ->
                val bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(
                    left = bars.left,
                    top = bars.top,
                    right = bars.right,
                    bottom = bars.bottom
                )
                insets
            }
        }
    }

}

