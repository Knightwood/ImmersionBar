package android.accompanist.dialoghelper.utils

import android.R
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.FragmentActivity

object TransparentActivityHelper {
    /**
     * 使用方式：
     *
     * 1. 此方法要在setContentView之前调用
     *
     * ```
     * override fun onCreate(savedInstanceState: Bundle?) {
     *     TransparentActivityHelper.setUp( this)
     *     super.onCreate(savedInstanceState)
     * }
     * ```
     *
     * 2. activity需要在manifest中指定一个具有windowIsTranslucent属性的主题
     * ```
     * <style name="Theme.Material3.Transparent" parent="Theme.Material3.DayNight.NoActionBar">
     *     <item name="android:windowIsTranslucent">true</item>
     * </style>
     * ```
     */
    fun setUp(activity: ComponentActivity) {
        activity.run {
            enableEdgeToEdge()
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            window.setBackgroundDrawableResource(R.color.transparent)
        }
    }
}
