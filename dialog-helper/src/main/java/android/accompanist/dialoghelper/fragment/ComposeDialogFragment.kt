package android.accompanist.dialoghelper.fragment

import android.accompanist.dialoghelper.component.ComposeUI
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment

/**
 * ```
 * fun example(){
 *     val dialog = ComposeDialogFragment()
 *     dialog.setContentView {}
 *     dialog.show(supportFragmentManager, "ComposeDialogFragment")
 * }
 * ```
 * 或者
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
    /**
     * 使用state存储布局，方便任意时刻更改布局内容，
     * 避免了一开始为调用[setContentView]，在onCreateView调用后再调用[setContentView]，ComposeView不能实时更新。
     */
    private var composeUIState: MutableState<ComposeUI?> = mutableStateOf(null)
    private val rootView by lazy {
        ComposeView(requireContext()).apply {
            setContent {
                composeUIState.value?.invoke()
            }
        }
    }

    fun setContentView(composeUI: ComposeUI) {
        this.composeUIState.value = composeUI
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }

}
