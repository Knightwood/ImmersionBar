package android.accompanist.dialoghelper.component_dialog

import android.R
import android.accompanist.dialoghelper.utils.ViewBindingHelper
import android.accompanist.dialoghelper.utils.WindowSecureFlagPolicy
import android.accompanist.dialoghelper.utils.backgroundDim
import android.accompanist.dialoghelper.utils.setSecureFlag
import android.accompanist.dialoghelper.utils.transparentBackground
import android.app.Dialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.viewbinding.ViewBinding

object ComponentDialogHelper

/**
 * 使用ViewBinding绑定dialog已加载的View
 * 需要在dialog显示之后调用
 * 例如：
 * ```
 * val dialog = ComponentDialog(context)
 * dialog.setContentView(R.layout.dialog_main)
 * dialog.show()
 * dialog.withBinding<DialogMainBinding>(R.layout.dialog_main) {
 *
 * }
 * ```
 * 或者
 * ```
 * val dialog = MaterialAlertDialogBuilder(context)
 *     .setTitle("标题")
 *     .setMessage("内容")
 *     .setView(R.layout.dialog_main)
 *     .setPositiveButton("确定") { dialog, _ ->
 *         dialog.dismiss()
 *     }.show()
 *
 * dialog.withBinding<DialogMainBinding>(
 *     R.id.dialog_root
 * ) {
 *     tv1.text = "微软"
 * }
 *
 * ```
 * @param layoutId Int? 你调用Dialog.setView设置的布局的id。
 * 如果传入null，则使用android.R.content中的第一个子视图进行绑定，这可能会有bug。
 * @param block T.() -> Unit 使用ViewBinding做点什么吧
 */
inline fun <reified T : ViewBinding> Dialog.withBinding(
    layoutId: Int? = null,
    block: T.() -> Unit,
) {
    runCatching {
        window?.let { window ->
            val v =
                if (layoutId != null) {
                    window.decorView.findViewById<View>(layoutId)
                } else {
                    window.decorView.findViewById<FrameLayout>(R.id.content).getChildAt(0)
                }
            val binding: Any? = window.decorView.getTag(android.accompanist.dialoghelper.R.id.oh_view_binding)
            if (binding != null && binding is T) {
                binding.block()
            } else {
                val b = ViewBindingHelper.bind<T>(v).getOrNull()
                if (b != null) {
                    window.decorView.setTag(android.accompanist.dialoghelper.R.id.oh_view_binding, b)
                    b.block()
                }
            }
        }
    }.onFailure { throwable ->
        Log.d("Dialog", "withBinding: ${throwable.cause}", throwable)
    }
}

/**
 * 使用ViewBinding加载布局，并设置给Dialog
 * 例如：
 * ```
 * val dialog = ComponentDialog(context)
 * dialog.setContentView<DialogMainBinding>{
 *
 * }
 * dialog.show()
 * ```
 */
inline fun <reified T : ViewBinding> Dialog.setContentView(
    block: T.() -> Unit,
) {
    val binding = ViewBindingHelper.inflate<T>(context).getOrNull()
    if (binding != null) {
        this.setContentView(binding.root)
        binding.block()
    }
}

/**
 * 为什么接受者类型是ComponentDialog而不是Dialog：
 * ComposeView需要向上找到一个有LifecycleOwner tag的view，获取到Lifecycle
 * 也就是调用如下代码
 * window!!.decorView.setViewTreeLifecycleOwner(this)
 * 然而，Dialog根本没有实现LifecycleView，他的decorView也就没有设置LifecycleOwner这个tag
 *
 * 例如：
 * ```
 * val dialog = ComponentDialog(context)
 * dialog.setContentView{
 *
 * }
 * dialog.show()
 * ```
 */
fun ComponentDialog.setContentView(
    lp: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    ),
    composeUI: @Composable () -> Unit,
) {
    //弹窗默认有一个白色背景，设置Compose视图，这个白色背景很碍事
    transparentBackground()
    val root = ComposeView(context).apply {
        setContent {
            composeUI()
        }
    }
    setContentView(root, lp)
}


/**
 *@param dim  0-1 之间 如果设置为1 就是全黑色了
 */
fun Dialog.backgroundDim(dim: Float = 0.3f) {
    window?.backgroundDim(dim)
}

fun Dialog.setSecureFlag(flag: Int = WindowSecureFlagPolicy.NONE) {
    window?.setSecureFlag(flag)
}

/**
 * /弹窗默认有一个白色背景，设置Compose视图，这个白色背景很碍事
 * 可以使用此方法去除背景
 *
 * 如果在DialogFragment、ComponentDialog使用Compose视图，
 * 会有个默认的白色背景，可以调用此方法去除背景。
 *
 * 在DialogFragment.onCreateDialog返回dialog时调用此方法
 * 在ComponentDialog中直接调用此方法
 */
fun Dialog.transparentBackground() = window?.transparentBackground()
