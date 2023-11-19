## 使用：

新方式：

```
注意：insets不是rect，四个值不是坐标，而是top指状态栏高度，bottom指导航栏高度，left和right表示两侧插入物宽度。

//以下两个方法都可以实现沉浸，但是在获取状态栏、导航栏高度上使用了不同的机制：

// 1.使用了 ViewCompat.setOnApplyWindowInsetsListener 设置监听事件
 * ViewCompat.setOnApplyWindowInsetsListener： 与 View 的事件分发一样，WindowInsets 的分发也是 N 叉树的遍历过程：
 * 从 N 叉树的根节点（DecoView）开始，按照 深度优先 的方式分发给 子 view。
 * Android 10 和 Android 11 两个版本官方连续修改了 ViewGroup#dispatchApplyWindowInsets() 的逻辑
 *
 * targetSdkVersion < 30 如果某个节点消费了 Insets，所有没遍历到的节点都不会收到 WindowInsets 的分发，
 * 所以旧版本无法做到两个同级的 View 同时消费 WindowInsets
 *
 * 当 app 运行在 Android 11 以上版本的设备上且 targetSdkVersion >= 30，
 * 如果某个节点消费了 Insets，该节点的所有子节点不会收到 WindowInsets 分发，但它的平级的view及其子view仍有机会消费事件。
 *
quickImmersion(window.decorView, false) {it:Insets->
	//手动更改view的margin或padding
	//更改视图的padding
	content.updatePadding(top = it.top)
	//更改margin
	content.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = systemBarInsets.top
	}
}

// 2.使用了 ViewCompat.getRootWindowInsets(view) 读取 WindowInsets从而得到状态栏或导航栏高度，不涉及监听及分发。
quickImmersion(ignoringVisibility = false) {it:Insets->
	//手动更改view的margin或padding
	//更改视图的padding
	content.updatePadding(top = it.top)
	//更改margin
	content.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = systemBarInsets.top
	}
}

//还有很多的扩展方法可以快捷的改变状态栏或导航栏的设置
例如：
//手动将状态栏设置为浅色主题，将得到深色的文字和图标
statusBarTheme(ThemeType.LIGHT)

//获取状态栏或导航栏高度
注意：该方法返回分发给视图树的原始 insets
 * Insets 只有在 view attached 才是可用的
 * API 20 及以下 永远 返回 false
 *
 * ViewCompat.getRootWindowInsets需要viewattach之后才会有用。
 *   何时才会attach：
 *   当Activity的onResume()在第一次被调用之后，View.dispatchAttachedToWindow才会被执行，也就是attached操作。
 *   因此，可以把此方法的调用放进 View.post()，或是ktx扩展库的View.doOnAttach()方法
getSystemBarInsets()

//更改屏幕亮度，范围为0~1.0,1.0时为最亮，-1为系统默认设置
windowBrightness=0.5
```







旧方式：

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
ImmersionNavBar()
ImmersionNavBar{
//配置
}
```