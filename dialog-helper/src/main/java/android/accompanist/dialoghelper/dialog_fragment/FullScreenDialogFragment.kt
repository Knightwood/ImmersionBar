package android.accompanist.dialoghelper.dialog_fragment

import android.app.Dialog
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import android.accompanist.dialoghelper.component_dialog.ComposeUI
import android.accompanist.dialoghelper.component_dialog.FullScreenDialog
import android.accompanist.dialoghelper.component_dialog.transparentBackground

open class FullScreenDialogFragment : DialogFragment() {
    /**
     * DialogFragment虽然是一个Fragment，
     * 但是他会把onCreateView返回的布局加载到[onCreateDialog]返回的弹窗（弹窗中的窗口）中
     * 我们只要在这里将返回的默认弹窗替换成沉浸式弹窗就能实现DialogFragment沉浸目的
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return FullScreenDialog(requireContext())
    }

}

/**
 * 在DialogFragment中返回ComposeUI
 * ```
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
 *         return contentView {
 *
 *         }
 *     }
 * ```
 */
fun DialogFragment.contentView(
    attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    content: ComposeUI,
): View {
    dialog?.transparentBackground()//去掉背景
    return (this as Fragment).contentView(attrs, defStyleAttr, content)
}

/**
 * 在Fragment中设置ComposeUI
 *
 * ps:可以获取window并调用[transparentBackground]，使window透明
 *
 * ```
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
 *         return contentView {
 *
 *         }
 *     }
 * ```
 */
fun Fragment.contentView(
    attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    content: ComposeUI,
): View {
    return ComposeView(
        requireContext(),
        attrs,
        defStyleAttr
    ).apply {
        setContent {
            content()
        }
    }
}
