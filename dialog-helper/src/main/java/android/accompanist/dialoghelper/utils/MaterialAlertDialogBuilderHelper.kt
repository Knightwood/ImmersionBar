package android.accompanist.dialoghelper.utils

import androidx.compose.ui.platform.ComposeView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.accompanist.dialoghelper.component.ComposeUI
import android.accompanist.dialoghelper.databinding.AlertDialogInputEditBinding
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding


/**
 * 链式调用在遇到一些根据条件调用的场景有些不好用，
 * 所以这里提供DSL方式
 */
fun MaterialAlertDialogBuilder.dsl(action: MaterialAlertDialogBuilder.() -> Unit): MaterialAlertDialogBuilder {
    this.apply(action)
    return this
}

fun MaterialAlertDialogBuilder.setView(ui: ComposeUI): MaterialAlertDialogBuilder {
    setView(ComposeView(context).apply {
        setContent {
            ui()
        }
    })
    return this
}

/**
 * 给弹窗设置一个输入框view
 * @param action 对输入框的操作
 */
fun MaterialAlertDialogBuilder.setInputView(action: AlertDialogInputEditBinding.() -> Unit): MaterialAlertDialogBuilder {
    val binding =
        AlertDialogInputEditBinding.inflate(LayoutInflater.from(context), null, false)
    setView(binding.root)
    binding.action()
    return this
}

/**
 * 通过反射，inflateView实例，生成ViewBinding实例，并将ViewBinding的根视图设置给弹窗
 */
inline fun <reified T : ViewBinding> MaterialAlertDialogBuilder.setView(action: T.() -> Unit): MaterialAlertDialogBuilder {
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java)
    val obj = method.invoke(null, LayoutInflater.from(context)) as T
    setView(obj.root)
    obj.action()
    return this
}
