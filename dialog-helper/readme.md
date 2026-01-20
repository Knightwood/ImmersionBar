# 功能

## ComponentDialog、Dialog增强

ComponentDialog是Dialog的子类，Dialog类不支持加载ComposView。
原因：
Dialog类没有生命周期，也就没法把ViewTreeLifecycleOwner实例作为tag设置给DecorView。
ComposeView生成时又需要从view tree中找到有ViewTreeLifecycleOwner tag的view，获取lifecycle实例， 自然Dialog加载ComposeView会报错。

ComponentDialog给decorView设置tag
```kotlin
window!!.decorView.setViewTreeLifecycleOwner(this)
window!!.decorView.setViewTreeOnBackPressedDispatcherOwner(this)
window!!.decorView.setViewTreeSavedStateRegistryOwner(this)
```


当我们使用ComponentDialog时，通常有两种方式

- 使用`setContentView`方法设置布局

```kotlin
val dialog = ComponentDialog(context)
dialog.setContentView(R.layout.dialog_main)
dialog.show()
```

- 继承ComposeDialogFragment，使用setContentView方法设置布局

```kotlin
class DialogExample(context: Context) : ComponentDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //允许按返回键关闭弹窗
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(R.layout.dialog_main)
    }
}
```

**增强功能**

1. 设置compose ui

```kotlin
val dialog = ComponentDialog(context)
dialog.setContentView {
    Text("Compose内容")
}

//或者
class DialogExample(context: Context) : ComponentDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView() {
            Text("Compose内容")
        }
    }
}
```

2. 使用ViewBinding绑定已加载的布局

```kotlin
class DialogExample(context: Context) : ComponentDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.id.dialog_root)
    }
}

val dialog = DialogExample(context)
dialog.withBinding<DialogMainBinding>(R.id.dialog_root) {
    tv1.text = "微软"
}
```

3. 使用ViewBinding加载布局并设置：

```kotlin
 val dialog = ComponentDialog(context)
dialog.setContentView<DialogMainBinding> {

}
dialog.show()
```

## MaterialAlertDialogBuilder增强

首先，我们生成一个`MaterialAlertDialogBuilder`对象：

```kotlin
val builder = MaterialAlertDialogBuilder(context)
```

**增强功能**

1. 使用dsl形式构建：

```kotlin
builder.dsl {
    setTitle("标题")
    setMessage("内容")
    setPositiveButton("确定") { dialog, _ ->
        dialog.dismiss()
    }
    setView {
        Text("Compose内容")
    }
}.show()
```

2. 使用`setView`方法设置ComposeUI：

```kotlin
builder.setView {
    Text("Compose内容")
}
```

3. 使用withBinding绑定已加载的布局
   由于`MaterialAlertDialogBuilder`最终build之后返回的就是一个普通的AlertDialog类（ComponentDialog的子类）
   我们可以直接使用ComponentDialog的增强函数（回看上一节：ComponentDialog、Dialog增强中的withBinding）

```kotlin
builder.setTitle("标题")
    .setMessage("内容")
    .setView(R.layout.dialog_main)
    .setPositiveButton("确定") { dialog, _ ->
        dialog.dismiss()
    }.show()

dialog.withBinding<DialogMainBinding>(R.id.dialog_root) {
    tv1.text = "微软"
}
```

4. 使用ViewBinding加载布局并设置：

```kotlin
builder.setView<DialogMainBinding> {
    tv1.text = "微软"
}
```

5. 预定义的输入弹窗

```kotlin
builder.setTitle("标题")
    .setMessage("内容")
    .setInputView {
        this.textInputText.setText("默认值")
        this.textInputText.addTextChangedListener {

        }
    }
    .setPositiveButton("确定") { dialog, _ ->
        dialog.dismiss()
    }.show()
```

## DialogFragment增强

### ComposeDialogFragment

1. 使用setContentView方法，便于在任意时刻加载布局并更改compose ui

此方法使得DialogFragment使用起来近似ComponentDialog， 不必先继承DialogFragment，重新onCreateView。

```kotlin
 val dialog = ComposeDialogFragment()
dialog.setContentView {
    Text("Compose内容")
}
dialog.show(supportFragmentManager, "ComposeDialogFragment")
```
### ViewDialogFragment

1. 使用setContentView方法，便于在任意时刻加载布局

 ```
 val dialog = ViewDialogFragment()
 dialog.show(supportFragmentManager, "ComposeDialogFragment")
 //直接加载布局id
 dialog.setContentView(R.layout.dialog_main)
 //或者使用ViewBinding
 dialog.setContentView<DialogMainBinding> {
     it.tv1.text = "微软"
 }
```

## 全屏弹窗

DialogFragment虽是一个Fragment，但他的内部原理是将onCreateView返回的布局放入内部通过onCreateDialog创建的弹窗中。
所以，针对DialogFragment的全屏化实现其实是针对内部onCreateDialog生成的ComponentDialog的全屏，因此功能实现起来与ComponentDialog如出一辙。

继承内置的全屏化弹窗基类即可实现全屏、透明弹窗
当然也可以不使用内置的透明弹窗基类，在你的弹窗类init块中调用`DialogFullScreenHelper.setUp(this)`
即可实现全屏、透明化弹窗。


### ComponentDialog、DialogFragment全屏

ComponentDialog

```kotlin
class FullScreenExampleDialog(context: Context) : FullScreenDialog(context) {
    private val d = this

    init {
        //增加暗色遮罩
        d.backgroundDim(defaultDim)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //允许按返回键关闭弹窗
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(
//            lp = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            ),// 测试点击R.id.content、或者ComposeView布局区域触发取消弹窗
        ) {
            TestDialogContent(
                dismiss = {
                    d.dismiss()
                },
                changeDim = {
                    d.backgroundDim(it)
                }
            )
        }
    }
}
```

DialogFragment

```kotlin
class FullScreenExampleDialogFragment : FullScreenDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            //可以调用此方法修改背景透明度
            backgroundDim(0.3f)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                TestDialogContent(
                    dismiss = {
                        dismiss()
                    },
                    changeDim = {
                        dialog?.backgroundDim(it)
                    }
                )
            }
        }
    }
}
 ```

## Activity全屏

首先给Activity设置一个具有`windowIsTranslucent`属性的style

```xml

<style name="Theme.Notex.Material3.Transparent">
    <item name="android:windowIsTranslucent">true</item>
</style>

```
然后调用`TransparentActivityHelper.setUp(this)`
```kotlin

class TransparentExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        TransparentActivityHelper.setUp(this)
        super.onCreate(savedInstanceState)
    }
}
```
