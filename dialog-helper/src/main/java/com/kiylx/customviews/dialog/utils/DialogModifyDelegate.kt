package jetbrains.compose.accompanist.ui.components.dialog

import androidx.activity.ComponentDialog
import androidx.activity.addCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

sealed interface IDialogDelegate {

}

/**
 * 仿照AppCompatDelegate，代理弹窗功能实现
 *
 * 主要实现功能
 * - 全屏、透明
 * - 设置compose内容视图
 */
class DialogModifyDelegate(
    val dialog: ComponentDialog,
) : IDialogDelegate {

    fun setComposeView(content: @Composable () -> Unit) {
        dialog.setContentView(
            ComposeView(dialog.context).apply {
                setContent(content)
            }
        )
    }

    fun enableOnBackPassedDismiss(onDismissRequest: () -> Unit) {
        dialog.run {
            // Due to how the onDismissRequest callback works
            // (it enforces a just-in-time decision on whether to update the state to hide the dialog)
            // we need to unconditionally add a callback here that is always enabled,
            // meaning we'll never get a system UI controlled predictive back animation
            // for these dialogs
            //支持返回键关闭弹窗
            onBackPressedDispatcher.addCallback(this) {
                onDismissRequest()
            }
        }
    }
}
