package com.kiylx.libx.windowinsinsets

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
FitsSystemWindows 的默认行为是：通过 padding 为 System bar 预留出空间


Android 30 引入了 WindowInsetsController 来控制 WindowInsets，主要功能包括：

1. 显示/隐藏 System bar
2. 设置 System bar 前景（如状态栏的文字图标）是亮色还是暗色
3. 逐帧控制 insets 动画，例如可以让软键盘弹出得更丝滑


## 使用：
```
//使用默认配置并沉浸
val immersionBar = ImmersionBar()
//或者手动配置，并沉浸
ImmersionBar {
//配置
}

//重新配置，并沉浸
immersionBar.reConfig {

}.immersion()
//导航栏，与状态栏是一样的配置方式，仅名字不同
ImmersionNavBar{}

```
 */

sealed class BaseConfig {
    /**
     * 判断传入的状态栏[navBarColor]和导航栏[statusBarColor]的背景颜色是亮色还是暗色，
     * 由此决定状态栏和导航栏的前景色是黑色还是白色，而忽略[navBarType]和[stateBarType]
     * 但这个过程还受到系统主题的影响。
     */
    var judgeColor: Boolean = false

    /**
     * 如果选择监听系统主题变更[listenSystemThemeChange]=true，
     * 这里将根据系统主题决定最终的状态栏主题
     *
     * 如果选择监听系统主题变更[listenSystemThemeChange]=false，
     * 这里将根据应用主题决定最终的状态栏主题
     */
    var careSystemTheme = false

    /**
     * 状态栏和导航栏是否设置相反的主题色
     */
    var reverse = false

    /**
     * 是否设置margin令视图不被状态栏覆盖
     */
    var dealInsets = true

    /**
     * 设置是否监听系统主题的变化
     */
    var listenSystemThemeChange = false

    /**
     * 让这些view避开状态栏，避免被状态栏/导航栏遮挡
     */
    var avoidIds: Array<Int> = emptyArray()

    /**
     * 浅色状态栏或是深色导航栏
     * 默认是浅色导航栏/导航栏，即文字和图标等前景色是黑色
     */
    var themeType: ThemeType = ThemeType.LIGHT

    /**
     * 状态栏/导航栏的背景色
     */
    var barColor: Int = Color.TRANSPARENT


}

class StateBarConfig : BaseConfig() {
    /**
     * 是否将内容扩展到全屏
     */
    var edgeToEdge = true
}

class NavBarConfig() : BaseConfig() {

}

abstract class Immersion<T : BaseConfig>(
    internal val fragmentActivity: FragmentActivity,
    val config: T,
) : LifecycleEventObserver {
    init {
        fragmentActivity.lifecycle.addObserver(this)
    }

    fun reConfig(block: T.() -> Unit): Immersion<T> {
        config.block()
        return this
    }

    /**
     * 实现沉浸
     */
    abstract fun immersion()

    /**
     * activity配置变更监听
     */
    internal var configChangedListener: Consumer<Configuration>? = null

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            ON_CREATE -> {
                immersion()
            }
            ON_DESTROY -> {
                //取消监听配置变化
                configChangedListener?.let {
                    fragmentActivity.removeOnConfigurationChangedListener(it)
                }
            }
            else -> {}
        }
    }

    /**
     * 根据应用的亮色或暗色主题，以及设置的状态栏颜色，决定给状态栏设置什么颜色
     * @param appTheme 当前的应用或系统主题是白（true）还是黑（false）
     */
    @SuppressLint("ObsoleteSdkInt")
    abstract fun FragmentActivity.changeBarTheme(appTheme: ThemeType)
}


class NavBarImmersion(
    fragmentActivity: FragmentActivity,
    config: NavBarConfig
) : Immersion<NavBarConfig>(fragmentActivity, config) {
    override fun immersion() {
        fragmentActivity.run {
            val rootView = findViewById<FrameLayout>(android.R.id.content)
            //1.颜色设置
            window.navigationBarColor = config.barColor

            //3.可能出现视觉冲突的 view 处理 insets
            if (config.dealInsets) {
                ViewCompat.setOnApplyWindowInsetsListener(rootView) { view_, windowInsetsCompat ->
                    // 此处更改的 margin，也可设置 padding，视情况而定

                    val navBarInsets =
                        windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())

                    config.avoidIds.forEach {
                        view_.findViewById<View>(it)
                            .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                bottomMargin = navBarInsets.bottom + 8
                            }
                    }

                    WindowInsetsCompat.CONSUMED
                }
            }
            //4.主题变化,文字图标等的前景色变化
            if (config.listenSystemThemeChange) {
                //监听系统主题变化
                configChangedListener = Consumer<Configuration> { t ->
                    changeBarTheme(adjustAppUiMode())//根据系统主题变色
                }
                this.addOnConfigurationChangedListener(configChangedListener!!)
            }
            changeBarTheme(adjustAppUiMode())//根据应用主题变色
        }
    }

    /**
     * 根据应用的亮色或暗色主题，以及设置的状态栏颜色，决定给状态栏设置什么颜色
     * @param appTheme 当前的应用或系统的主题是白（true）还是黑（false）
     */
    override fun FragmentActivity.changeBarTheme(appTheme: ThemeType) {
        changeNavBarTheme(appTheme)
    }

    private fun FragmentActivity.changeNavBarTheme(appTheme: ThemeType) {
        //根据传入设置与当前的主题决定设置light(true)或是dark(false)主题
        var b = if (config.judgeColor) {
            if (config.careSystemTheme) {
                ColorUtils.isColorDark(config.barColor) && appTheme.b
            } else {
                ColorUtils.isColorDark(config.barColor)
            }
        } else {
            if (config.careSystemTheme) {
                config.themeType.b && appTheme.b
            } else {
                config.themeType.b
            }
        }
        //是否反色
        if (config.reverse) b = !b
        //改变导航栏的前景色
        navBarTheme(if (b) ThemeType.LIGHT else ThemeType.DARK)
    }

}

class ImmersionBar(
    fragmentActivity: FragmentActivity,
    config: StateBarConfig
) : Immersion<StateBarConfig>(fragmentActivity, config), LifecycleEventObserver {

    override fun immersion() {
        fragmentActivity.run {
            val rootView = findViewById<FrameLayout>(android.R.id.content)
            //1.颜色设置
            window.statusBarColor = config.barColor
            //2.内容全屏
            if (config.edgeToEdge) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 使内容区域全屏
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                } else {
                    rootView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            )
                }
            }
            //3.可能出现视觉冲突的 view 处理 insets
            if (config.dealInsets) {
                ViewCompat.setOnApplyWindowInsetsListener(rootView) { view_, windowInsetsCompat ->
                    // 此处更改的 margin，也可设置 padding，视情况而定
                    val systemBarInsets =
                        windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())

                    //让顶部视图下移，避免被状态栏覆盖
                    config.avoidIds.forEach {
                        view_.findViewById<View>(it)
                            .updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                topMargin = systemBarInsets.top
                            }
                    }

                    WindowInsetsCompat.CONSUMED
                }
            }
            //4.主题变化,文字图标等的前景色变化
            if (config.listenSystemThemeChange) {
                //监听系统主题变化
                configChangedListener = Consumer<Configuration> { t ->
                    changeBarTheme(adjustAppUiMode())//根据应用主题变色
                }
                this.addOnConfigurationChangedListener(configChangedListener!!)
            }
            changeBarTheme(adjustAppUiMode())//根据应用主题变色
        }
    }

    /**
     * 根据应用的亮色或暗色主题，以及设置的状态栏颜色，决定给状态栏设置什么颜色
     * @param appTheme 当前的系统主题是白（true）还是黑（false）
     */
    override fun FragmentActivity.changeBarTheme(appTheme: ThemeType) {
        changeStateBarTheme(appTheme)
    }

    /**
     * @param sysTheme 系统主题是黑还是白
     */
    private fun FragmentActivity.changeStateBarTheme(sysTheme: ThemeType) {
        //根据传入设置与当前的主题决定设置light(true)或是dark(false)主题
        var b = if (config.judgeColor) {
            if (config.careSystemTheme) {
                ColorUtils.isColorDark(config.barColor) && sysTheme.b
            } else {
                ColorUtils.isColorDark(config.barColor)
            }
        } else {
            if (config.careSystemTheme) {
                config.themeType.b && sysTheme.b
            } else {
                config.themeType.b
            }
        }
        //是否反色
        if (config.reverse) b = !b

        //根据背景色主题改变状态栏的前景色
        statusBarTheme(if (b) ThemeType.LIGHT else ThemeType.DARK)
    }
}

// <editor-fold desc="沉浸式状态栏">

/**
 * 默认是浅色主题
 */
@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
inline infix fun Fragment.ImmersionBar(block: StateBarConfig.() -> Unit): ImmersionBar {
    return requireActivity().ImmersionBar(block)
}

@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
fun Fragment.ImmersionBar(): ImmersionBar {
    return this ImmersionBar {}
}

/**
 * 默认是浅色主题
 */
@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
inline infix fun FragmentActivity.ImmersionBar(block: StateBarConfig.() -> Unit): ImmersionBar {
    val config = StateBarConfig()
    config.block()
    return ImmersionBar(this, config)
}

@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
fun FragmentActivity.ImmersionBar(): ImmersionBar {
    return this ImmersionBar {}
}
//</editor-fold>

// <editor-fold desc="沉浸式导航栏">

/**
 * 默认是浅色主题
 */
@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
inline infix fun Fragment.ImmersionNavBar(block: NavBarConfig.() -> Unit): NavBarImmersion {
    return requireActivity().ImmersionNavBar(block)
}

@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
fun Fragment.ImmersionNavBar(): NavBarImmersion {
    return this ImmersionNavBar {}
}

/**
 * 默认是浅色主题
 */
@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
inline infix fun FragmentActivity.ImmersionNavBar(block: NavBarConfig.() -> Unit): NavBarImmersion {
    val config = NavBarConfig()
    config.block()
    return NavBarImmersion(this, config)
}

@Deprecated(message = "用quickImmersion取代", replaceWith = ReplaceWith("this quickImmersion {}"))
fun FragmentActivity.ImmersionNavBar(): NavBarImmersion {
    return this ImmersionNavBar {}
}
//</editor-fold>