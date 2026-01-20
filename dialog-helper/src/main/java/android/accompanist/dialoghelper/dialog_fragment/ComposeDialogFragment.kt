package android.accompanist.dialoghelper.dialog_fragment

import android.accompanist.dialoghelper.component_dialog.ComposeUI
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentDialog
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment

/**
 * 通常我们需要继承DialogFragment，重写onCreateDialog提供自定义Dialog;重写onCreateView提供自定义视图;
 *
 * 此类将简化DialogFragment使用，不必继承DialogFragment重写onCreateView即可使其加载Compose视图、提供自定义Dialog
 *
 *
 * 示例：
 * ```
 * fun example(){
 *     val dialog = ComposeDialogFragment()
 *     //设置自定义dialog
 *     dialog.setDialog(ComponentDialog(requireContext()))
 *     //设置Compose视图
 *     dialog.setContentView {}
 *     //显示
 *     dialog.show(supportFragmentManager, "ComposeDialogFragment")
 * }
 * ```
 *
 *
 * 或者继承ComposeDialogFragment后，可以在任意函数中更新Compose视图
 * ```
 * class ExampleDialogFragment : ComposeDialogFragment() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView {
 *             Text("Compose内容")
 *         }
 *     }
 * }
 * ```
 */
open class ComposeDialogFragment : DialogFragment() {
    private var mDialog: ComponentDialog? = null

    /**
     * 使用state存储布局，方便任意时刻更改布局内容，
     * 避免了一开始为调用[setContentView]，在onCreateView调用后再调用[setContentView]，ComposeView不能实时更新。
     */
    private var composeUIState: MutableState<ComposeUI?> = mutableStateOf(null)
    private val rootComposeView by lazy {
        ComposeView(requireContext()).apply {
            setContent {
                composeUIState.value?.invoke()
            }
        }
    }

    /**
     * 替换Compose视图内容，可以在任意位置、生命周期阶段调用。
     */
    fun setContentView(composeUI: ComposeUI) {
        this.composeUIState.value = composeUI
    }

    /**
     * 替换默认的Dialog，但是需要在[onCreateDialog]方法被调用之前设置
     */
    fun setDialog(dialog: ComponentDialog) {
        mDialog = dialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (mDialog != null) return mDialog!!
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootComposeView
    }

}
