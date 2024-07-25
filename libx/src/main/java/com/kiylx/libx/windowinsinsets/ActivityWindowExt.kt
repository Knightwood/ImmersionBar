package com.kiylx.libx.windowinsinsets

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.core.graphics.Insets
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnAttach
import androidx.core.view.updatePadding

/*

WindowInsetsCompat.Type.ime() //键盘
WindowInsetsCompat.Type.statusBars() //状态栏
WindowInsetsCompat.Type.navigationBars() //导航栏
WindowInsetsCompat.Type.systemBars()  //状态栏、导航栏和标题栏

* */

//<editor-fold desc="沉浸快速方法">
/**
 * onConfigurationChanged(Configuration newConfig)：当系统的配置信息发生改变时，系统会调用此方法。
 * 注意，只有在配置文件 AndroidManifest 中处理了 configChanges属性
 * 对应的设备配置，该方法才会被调用。 如果发生设备配置与在配置文件中设置的不一致，则Activity会被销毁并使用新的配置重建。
 */
fun FragmentActivity.listenConfigurationChanged(func: (t: Configuration) -> Unit) {
    val configChangedListener = Consumer<Configuration> { t: Configuration ->
        func(t)
    }
    this.addOnConfigurationChangedListener(configChangedListener)
}

/**
 * @param consumed 是否消费掉insets分发事件，致使不在向下传递。
 */
fun Activity.quickImmersion(
    view: View = window.decorView,
    consumed: Boolean = false,
    func: (insets: Insets) -> Unit,
) {
    val themeType = adjustAppUiMode()
    edgeToEdge()
    systemBarTheme(themeType)
    fitSystemBarInsets(view, consumed, func)
}

fun Activity.quickImmersion(
    ignoringVisibility: Boolean = false,
    func: (insets: Insets) -> Unit,
) {
    val themeType = adjustAppUiMode()
    edgeToEdge()
    systemBarTheme(themeType)
    fitSystemBarInsets(ignoringVisibility, func)
}

/**
 * func 直接是用即可，不需要额外包裹在doOnAttach中
 */
fun Fragment.quickImmersion(
    ignoringVisibility: Boolean = false,
    func: (insets: Insets) -> Unit,
) {
    //状态栏和导航栏主题
    val themeType = requireActivity().adjustAppUiMode()
    systemBarTheme(themeType)
    //将内容扩展到全屏
    requireActivity().edgeToEdge()
    //读取insets
    fitSystemBarInsets(ignoringVisibility, func)
}

fun Fragment.fitSystemBarInsets(
    ignoringVisibility: Boolean = false,
    func: (insets: Insets) -> Unit,
) {
    val w = requireActivity().window
    requireActivity().findViewById<FrameLayout>(android.R.id.content).doOnAttach {
        if (ignoringVisibility) {
            func(w.getSystemBarInsetsIgnoringVisibility())
        } else {
            func(w.getSystemBarInsets())
        }
    }
}

//</editor-fold>

//<editor-fold desc="edge-to-edge">

fun Activity.edgeToEdge() {
    //1. 使内容区域全屏
    WindowCompat.setDecorFitsSystemWindows(window, false)
    //2. 设置 System bar 透明
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
}

/**
 * 与 View 的事件分发一样，WindowInsets 的分发也是 N 叉树的遍历过程： 从 N 叉树的根节点（DecoView）开始，按照
 * 深度优先 的方式分发给 子 view。 Android 10 和 Android 11 两个版本官方连续修改了
 * ViewGroup#dispatchApplyWindowInsets() 的逻辑
 *
 * targetSdkVersion < 30 如果某个节点消费了 Insets，所有没遍历到的节点都不会收到 WindowInsets 的分发，
 * 所以旧版本无法做到两个同级的 View 同时消费 WindowInsets
 *
 * 当 app 运行在 Android 11 以上版本的设备上且 targetSdkVersion >= 30， 如果某个节点消费了
 * Insets，该节点的所有子节点不会收到 WindowInsets 分发，但它的平级的view及其子view仍有机会消费事件。
 *
 * @param view 设置哪个view监听状态栏变化，这个变化的分发类似于触摸事件的分发。
 * @param consumed true：将消费掉这个状态栏/导航栏的insets事件不再向下传递
 * @param func
 *    得到system的高度后，可以使用View.updatePadding()或updateLayoutParams<ViewGroup.MarginLayoutParams>()
 *    改变某些视图的padding或margin
 */
fun Activity.fitSystemBarInsets(
    view: View,
    consumed: Boolean = false,
    func: (insets: Insets) -> Unit,
) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { view_, windowInsetsCompat ->
        //insets不是rect，四个值不是坐标，而是top指状态栏高度，bottom指导航栏高度，left和right表示两侧插入物宽度。

        // 得到 Insets{left=0, top=96, right=0, bottom=44}
        val systemBarInsets =
            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())

        // 得到 Insets{left=0, top=96, right=0, bottom=0}
//        val systemBarInsets2 =
//            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars())

        // 得到 Insets{left=0, top=0, right=0, bottom=44}
//        val systemBarInsets3 =
//            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())
//因此可以看出，根据传入的WindowInsetsCompat.Type的不同，得到的insets中，四个值有的存在，有的不存在

        func(systemBarInsets)
        if (consumed) {
            WindowInsetsCompat.CONSUMED
        } else {
            windowInsetsCompat
        }
    }
}

/**
 * 获取并根据需要自行处理状态栏遮挡问题 注：
 * 这个使用了ViewCompat.getRootWindowInsets。而它需要viewattach之后才会有用。 何时才会attach：
 * 当Activity的onResume()在第一次被调用之后，View.dispatchAttachedToWindow才会被执行，也就是attached操作。
 * 因此，可以调用需使用View.post()，或是ktx扩展库的View.doOnAttach()方法包装，此方法已内置
 *
 * @param ignoringVisibility true：即使状态栏、导航栏隐藏，依旧获取原始高度
 */
fun Activity.fitSystemBarInsets(
    ignoringVisibility: Boolean = false,
    func: (insets: Insets) -> Unit,
) {
    findViewById<FrameLayout>(android.R.id.content).doOnAttach {
        if (ignoringVisibility) {
            func(getSystemBarInsetsIgnoringVisibility())
        } else {
            func(getSystemBarInsets())
        }
    }

}


//</editor-fold>

/**
 * isAppearanceLightNavigationBars isAppearanceLightStatusBars true表示Light
 * Mode，状态栏字体呈黑色，反之呈白色
 */
//<editor-fold desc="亮度调节">

/**
 * 当前窗口亮度 范围为0~1.0,1.0时为最亮，-1为系统默认设置
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
var Activity.statusBarColor: Int
    get() = window.statusBarColor
    set(value) {
        window.statusBarColor = value
    }

var Fragment.statusBarColor: Int
    get() = requireActivity().statusBarColor
    set(value) {
        requireActivity().statusBarColor = value
    }

/**
 * 状态栏主题色 设置浅色，将得到黑色图标和文字
 *
 * @param type [ThemeType.LIGHT] 浅色主题，将得到深色的文字图标（文字、图标是深色）；
 */
infix fun Activity.statusBarTheme(type: ThemeType) {
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
 * 状态栏主题色 设置浅色，将得到黑色图标和文字
 */
infix fun Fragment.statusBarTheme(type: ThemeType) {
    requireActivity().statusBarTheme(type)
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
 * 导航栏主题色 设置浅色，将得到黑色图标和文字
 *
 * @param type [ThemeType.LIGHT] 浅色主题，将得到深色的文字和图标（文字、图标是深色）；
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
 * 导航栏主题色 设置浅色，将得到黑色图标和文字
 */
infix fun Fragment.navBarTheme(type: ThemeType) {
    requireActivity().navBarTheme(type)
}
//</editor-fold>

// <editor-fold desc="状态栏和导航栏">
/**
 * 全屏
 */
fun Activity.hideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowCompat.getInsetsController(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 退出全屏
 */
fun Activity.showSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowCompat.getInsetsController(window, window.decorView)
        .show(WindowInsetsCompat.Type.systemBars())
}


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
 * 导航栏和状态栏主题色 设置浅色，将得到黑色图标和文字
 */
infix fun Activity.systemBarTheme(type: ThemeType) {
    statusBarTheme(type)
    navBarTheme(type)
}

/**
 * 导航栏和状态栏主题色 设置浅色，将得到黑色图标和文字
 */
infix fun Fragment.systemBarTheme(type: ThemeType) {
    statusBarTheme(type)
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
fun Configuration.adjustThemeType(): ThemeType {
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
 * 而调用如下API则可以让系统认为我们拥有的是一个浅色的状态栏： 如此一来，状态栏上面的图标就会变成黑色的，以和浅色的状态栏相互映衬。
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


// <editor-fold desc="其他">


/**
 * 获取屏幕中的insets信息， Insets 对象拥有 4 个 int 值，用于描述显示区域矩形四个边的偏移：
 * 其中，top指状态栏的高度，bottom指导航栏的高度，left和right是两侧的插入物宽度
 * 当 System bar 隐藏时 getInsets() 获取的高度为 0，
 * 如果想在隐藏状态时也能获取高度，使用[getSystemBarInsetsIgnoringVisibility]方放
 *
 * 使用 ViewCompat.getRootWindowInsets(view) 获取 WindowInsets。请注意：
 * 该方法返回分发给视图树的原始 insets Insets 只有在 view attached 才是可用的 API 20 及以下 永远 返回
 * false
 *
 * 注：ViewCompat.getRootWindowInsets需要viewattach之后才会有用。 何时才会attach：
 * 当Activity的onResume()在第一次被调用之后，View.dispatchAttachedToWindow才会被执行，也就是attached操作。
 * 因此，可以把此方法的调用放进 View.post()，或是ktx扩展库的View.doOnAttach()方法
 */
fun Activity.getSystemBarInsets(): Insets {
    return window.getSystemBarInsets()
}

/**
 * 获取屏幕中的insets信息，即使处于隐藏状态，也可以获取高度 Insets 对象拥有 4 个 int 值，用于描述显示区域矩形四个边的偏移：
 * 其中，top指状态栏的高度，bottom指导航栏的高度，left和right是两侧的插入物宽度 当 System bar 隐藏时
 * getInsets() 获取的高度为 0
 *
 * 使用 ViewCompat.getRootWindowInsets(view) 获取 WindowInsets。请注意：
 * 该方法返回分发给视图树的原始 insets Insets 只有在 view attached 才是可用的 API 20 及以下 永远 返回
 * false
 *
 * 注：ViewCompat.getRootWindowInsets需要viewattach之后才会有用。 何时才会attach：
 * 当Activity的onResume()在第一次被调用之后，View.dispatchAttachedToWindow才会被执行，也就是attached操作。
 * 因此，可以把此方法的调用放进 View.post()，或是ktx扩展库的View.doOnAttach()方法
 */
fun Activity.getSystemBarInsetsIgnoringVisibility(): Insets {
    return window.getSystemBarInsetsIgnoringVisibility()
}


fun Window.getSystemBarInsetsIgnoringVisibility(): Insets {
    return ViewCompat.getRootWindowInsets(decorView)
        ?.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars()) ?: Insets.NONE
}

fun Window.getSystemBarInsets(): Insets {
    return ViewCompat.getRootWindowInsets(decorView)
        ?.getInsets(WindowInsetsCompat.Type.systemBars()) ?: Insets.NONE
}

/**
 * 获取屏幕的宽度
 */
fun FragmentActivity.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

//</editor-fold>

//<editor-fold desc="ime键盘">


fun Activity.hideSoftKeyboard() {
    WindowCompat.getInsetsController(window, findViewById<FrameLayout>(android.R.id.content))
        .hide(WindowInsetsCompat.Type.ime())
}

fun Activity.showSoftKeyboard() {
    WindowCompat.getInsetsController(window, findViewById<FrameLayout>(android.R.id.content))
        .show(WindowInsetsCompat.Type.ime())
}


//</editor-fold>
