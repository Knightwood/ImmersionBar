package com.kiylx.libx.windowinsinsets

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.palette.graphics.Palette
import androidx.core.graphics.ColorUtils


/**
 * isAppearanceLightNavigationBars
 * isAppearanceLightStatusBars
 * true表示Light Mode，状态栏字体呈黑色，反之呈白色
 */
//<editor-fold desc="亮度调节">

/**
 * 当前窗口亮度
 * 范围为0~1.0,1.0时为最亮，-1为系统默认设置
 */
var Activity.windowBrightness
    get() = window.attributes.screenBrightness
    set(brightness) {
        //小于0或大于1.0默认为系统亮度
        window.attributes = window.attributes.apply {
            screenBrightness = if (brightness > 1.0 || brightness < 0) -1.0F else brightness
        }
    }

infix fun Activity.brightnessTo(brightness: Float) {
    windowBrightness = brightness
}

//</editor-fold>

//<editor-fold desc="状态栏">
var Activity.stateBarColor: Int
    get() = window.statusBarColor
    set(value) {
        window.statusBarColor = value
    }

var Fragment.stateBarColor: Int
    get() = requireActivity().stateBarColor
    set(value) {
        requireActivity().stateBarColor = value
    }

/**
 * 状态栏主题色
 * 设置浅色，将得到黑色图标和文字
 * @param type [ThemeType.LIGHT] 浅色主题，将得到深色的前景色（文字、图标是深色）；
 *
 */
infix fun Activity.stateBarTheme(type: ThemeType) {
    val rootView = findViewById<FrameLayout>(android.R.id.content)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//api23 以上
        val controller = WindowCompat.getInsetsController(window, rootView)
        controller.isAppearanceLightStatusBars = type.b
    } else {
        if (type == ThemeType.LIGHT) {
            setLightStatusBar()
        } else {
            setDarkStatusBar()
        }
    }
}

/**
 * 状态栏主题色
 * 设置浅色，将得到黑色图标和文字
 */
infix fun Fragment.stateBarTheme(type: ThemeType) {
    requireActivity().stateBarTheme(type)
}

//</editor-fold>

// <editor-fold desc="导航栏">
var Activity.navBarColor: Int
    get() = window.navigationBarColor
    set(value) {
        window.navigationBarColor = value
    }

var Fragment.navBarColor: Int
    get() = requireActivity().navBarColor
    set(value) {
        requireActivity().navBarColor = value
    }

/**
 * 导航栏主题色
 * 设置浅色，将得到黑色图标和文字
 * @param type [ThemeType.LIGHT] 浅色主题，将得到深色的前景色（文字、图标是深色）；
 */
infix fun Activity.navBarTheme(type: ThemeType) {
    val rootView = findViewById<FrameLayout>(android.R.id.content)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//api23 以上
        val controller = WindowCompat.getInsetsController(window, rootView)
        controller.isAppearanceLightNavigationBars = type.b
    } else {
        if (type == ThemeType.LIGHT) {
            setLightNavBar()
        } else {
            setDarkNavBar()
        }
    }
}

/**
 * 导航栏主题色
 * 设置浅色，将得到黑色图标和文字
 */
infix fun Fragment.navBarTheme(type: ThemeType) {
    requireActivity().stateBarTheme(type)
}
//</editor-fold>

// <editor-fold desc="状态栏和导航栏">
/**
 * 设置状态栏和导航栏的背景颜色
 */
fun FragmentActivity.setSystemBarColor(
    stateBarColor: Int = Color.TRANSPARENT,
    navBarColor: Int = Color.TRANSPARENT,
) {
    //设置 System bar 颜色
    window.statusBarColor = stateBarColor
    window.navigationBarColor = navBarColor
}

/**
 * 导航栏和状态栏主题色
 * 设置浅色，将得到黑色图标和文字
 */
infix fun Activity.barTheme(type: ThemeType) {
    stateBarTheme(type)
    navBarTheme(type)
}

/**
 * 导航栏和状态栏主题色
 * 设置浅色，将得到黑色图标和文字
 */
infix fun Fragment.barTheme(type: ThemeType) {
    stateBarTheme(type)
    navBarTheme(type)
}

//</editor-fold>

// <editor-fold desc="应用主题">
/**
 * 判断应用主题
 */
fun Activity.adjustAppUiMode(): ThemeType {
    val uiMode = resources.configuration.uiMode
    if ((uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
        return ThemeType.DARK
    } else {
        return ThemeType.LIGHT
    }
}

/**
 * 判断系统主题
 */
fun Fragment.adjustSystemUiMode(): ThemeType {
    return requireActivity().adjustSystemUiMode()
}

fun Activity.adjustSystemUiMode(): ThemeType {
    val uiModeManager = (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager)
    if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
        return ThemeType.DARK
    } else {
        return ThemeType.LIGHT
    }
}

/**
 * 判断当前主题类型
 */
fun Configuration.themeType(): ThemeType {
    val currentNightMode = uiMode and Configuration.UI_MODE_NIGHT_MASK
    when (currentNightMode) {
        Configuration.UI_MODE_NIGHT_NO -> {
            // Night mode is not active, we're using the light theme
            return ThemeType.LIGHT
        }

        Configuration.UI_MODE_NIGHT_YES -> {
            // Night mode is active, we're using dark theme
            return ThemeType.DARK
        }
    }
    return ThemeType.LIGHT
}
//</editor-fold>

// <editor-fold desc="api23以下的方法">

/**
 * 而调用如下API则可以让系统认为我们拥有的是一个浅色的状态栏：
 * 如此一来，状态栏上面的图标就会变成黑色的，以和浅色的状态栏相互映衬。
 */
fun Activity.setLightStatusBar() {
    val flags = window.decorView.systemUiVisibility
    window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

fun Activity.setLightNavBar(color: Int = Color.WHITE) {
    if (isSupportNavBar()) {
        window.navigationBarColor = color
    }
}

/**
 * 如果要动态恢复成默认的深色状态栏，只需要这样设置：
 */
fun Activity.setDarkStatusBar() {
    val flags = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

fun Activity.setDarkNavBar(color: Int = Color.BLACK) {
    if (isSupportNavBar()) {
        window.navigationBarColor = color
    }
}

/**
 * Return whether the navigation bar visible.
 */
fun Activity.isSupportNavBar(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val wm = windowManager
        val display: Display = wm.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        return realSize.y !== size.y || realSize.x !== size.x
    }
    val menu = ViewConfiguration.get(this.applicationContext).hasPermanentMenuKey()
    val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    return !menu && !back
}

//</editor-fold>


// <editor-fold desc="获取状态栏信息方法">
/**
 * 获取状态栏的宽度
 */
fun FragmentActivity.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

/**
 * 获取状态栏高度
 */
fun FragmentActivity.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

//</editor-fold>
