# `windowIsFloating`

`windowIsFloating` 是一个 Android 主题属性，主要用于定义 Activity 窗口是否为浮动样式。

## 主要作用

- **控制窗口外观**：设置 Activity 是否显示为浮动窗口样式
- **影响窗口行为**：改变 Activity 的显示和交互方式

## 典型用途

- **对话框样式 Activity**：创建类似对话框的界面
- **悬浮窗效果**：实现半透明或圆角窗口效果
- **弹出式界面**：显示临时性的浮层内容

## 常见配置

```xml
<style name="Theme.App.Dialog" parent="Theme.Material3.DayNight">
    <item name="android:windowIsFloating">true</item>
</style>
```


## 视觉效果

- 当值为 `true` 时，通常会呈现：
    - 圆角边框
    - 阴影效果
    - 半透明背景
    - 对话框样式的外观

- 当值为 `false` 时，Activity 会占据全屏或根据其他布局参数显示

这个属性常用于自定义对话框主题或需要特殊窗口效果的场景。

# `windowLayoutInDisplayCutoutMode`

`windowLayoutInDisplayCutoutMode` 是 Android 中的一个窗口布局属性，用于控制应用内容如何处理屏幕上的刘海区域（凹口）。

## 主要作用

- **控制刘海区域布局行为**：决定应用内容是否延伸到屏幕的刘海或挖孔区域
- **适配不同屏幕形态**：确保应用在各种异形屏设备上正常显示

## 可选值

### `LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT`
- 默认模式，系统根据应用特性自动决定布局策略

### `LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER`
- 内容永远不会延伸到刘海区域，系统会在四周添加黑边保护

### `LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES`
- 允许内容延伸到短边的刘海区域（如竖屏时的顶部刘海）

### `LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS`
- 总是允许内容延伸到刘海区域，需自行处理遮挡问题

## 使用方式

```xml
<style name="Theme.App" parent="Theme.Material3.DayNight">
    <item name="android:windowLayoutInDisplayCutoutMode">
        LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    </item>
</style>
```

---

# 解决在圆角手机(如小米8)上自定义Dialog无法全屏的问题

https://www.cnblogs.com/whatCode/p/10209685.html

在小米8等一系列圆角的手机上测试项目时，发现我的自定义dialog无法全屏了，这时我的dialog全屏的解决方案还是和网上大部分人是一样的

```java
        Window window = getWindow();
        if (window == null) return;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(gravity);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
```
但是当手机使用了圆角设计时，可能就会为了显示效果而强制使dialog能够完整显示。

常规做法可能是在其周围加上padding。但是通过我的代码可以看到，我已经使这个DecorView的padding为0，显示效果仍不理想。

事实上这时view已经占满全屏，你可明显的发现view下部有下移的痕迹。

因为我还没有来得及深入了解其底层原因，只能按照我自己的猜测去尝试。

经过对我的代码进行分析，我猜测可能是系统强制为dialog设置了setClipBounds，于是我尝试使用了setClipToOutline(false)来取消它的clip，发现它确实是可用的。

于是我得到了一个解决方案，在以上代码中添加

window.getDecorView().setClipToOutline(false);

---






谷歌之所以禁用默认的cancelable功能，
是因为AlertDialog是一个compose函数，我们通过一个State控制弹窗显示和隐藏
比如：
```
 var dialogState by remember { mutableStateOf(false) }
 if(dialogState) {
      AlertDialog(onDismissRequest={dialogState=false} )
 }
 ```
而内部行为则是：
```
     DisposableEffect(dialog) {
         dialog.show()

         onDispose {
             dialog.dismiss()
             dialog.disposeComposition()
         }
     }
 ```
也就是说，当你的dialogState为false时，触发清理，弹窗会关闭，compose视图会卸载，
如果不禁用ComponentDialog实现的cancel行为，会发现弹窗隐藏时无法同步你的状态，compose实现的弹窗视图没有卸载

但我们实现的弹窗类不需要这么做，因为我们的弹窗显示会使用点击行为触发，不在compose函数中直接调用。
如此，compose内容仅作为弹窗的内容视图，其生命周期跟随弹窗。而不是像AlertDialog那样：弹窗的生命周期要跟随compose节点的生命周期。

