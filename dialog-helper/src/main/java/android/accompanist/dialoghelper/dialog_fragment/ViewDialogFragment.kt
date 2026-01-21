package android.accompanist.dialoghelper.dialog_fragment

import android.accompanist.dialoghelper.utils.ViewBindingHelper
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.lifecycle.withCreated
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch

/**
 * 自定义对话框片段基类，支持自定义Dialog和View内容
 *
 * 通常我们需要继承DialogFragment，重写onCreateDialog提供自定义Dialog;重写onCreateView提供自定义视图;
 *
 * 我们可以使onCreateView固定返回一个FrameLayout（称为rootView），之后可以在任意位置、生命周期阶段调用[ViewDialogFragment.setContentView]方法，
 * 将新的内容视图添加到rootView，使其不需要继承DialogFragment并重写onCreateView才能显示布局。
 *
 * 实现了上面加载布局的新方式之后，我们还需改写onCreateDialog，使其不需要继承DialogFragment并重写onCreateDialog方法以替换Dialog
 * 因此，提供了一个[ViewDialogFragment.setDialog]方法辅助达成此目的。
 *
 * ```
 * val dialog = ViewDialogFragment()
 * dialog.show(supportFragmentManager, "ComposeDialogFragment")
 * //直接加载布局id
 * dialog.setContentView(R.layout.dialog_main)
 * //或者使用ViewBinding
 * dialog.setContentView<DialogMainBinding> {
 *     it.tv1.text = "微软"
 * }
 *```
 */
open class ViewDialogFragment : DialogFragment() {
    private var mDialog: ComponentDialog? = null

    /**
     * 根布局视图，懒加载创建FrameLayout作为容器
     */
    val rootView by lazy {
        createFrameLayout()
    }

    /**
     * 创建FrameLayout根布局
     * @return 创建的FrameLayout实例
     */
    private fun createFrameLayout(): FrameLayout {
        return FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    /**
     * 替换默认的Dialog，但是需要在[onCreateDialog]方法被调用之前设置
     * @param dialog 要设置的ComponentDialog实例
     */
    fun setDialog(dialog: ComponentDialog) {
        mDialog = dialog
    }

    /**
     * 创建对话框实例
     * @param savedInstanceState 保存的状态数据
     * @return 对话框实例
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (mDialog != null) return mDialog!!
        return super.onCreateDialog(savedInstanceState)
    }

    /**
     * 创建视图内容
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的状态数据
     * @return 根视图
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }

    @PublishedApi
    internal fun setContentViewInternal(view: View) {
        require(rootView.childCount == 0) {
            "rootView already has a child, cannot add new content view"
        }
        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // 添加新视图到根布局
        rootView.addView(view, lp)
    }

    /**
     * 设置对话框内容视图
     * @param view 要设置的内容视图
     */
    fun setContentView(view: View) {
        this.lifecycleScope.launch {
            lifecycle.withCreated {
                setContentViewInternal(view)
            }
        }
    }

    /**
     * 通过布局资源ID设置对话框内容视图
     * @param layoutId 布局资源ID
     */
    fun setContentView(layoutId: Int) {
        this.lifecycleScope.launch {
            lifecycle.withCreated {
                require(rootView.childCount == 0) {
                    "rootView already has a child, cannot add new content view"
                }
                LayoutInflater.from(requireContext()).inflate(layoutId, rootView, true)
            }
        }
    }

    /**
     * 通过ViewBinding设置对话框内容视图
     * @param T ViewBinding类型参数
     * @param action ViewBinding操作回调
     */
    inline fun <reified T : ViewBinding> setContentView(crossinline action: T.() -> Unit) {
        this.lifecycleScope.launch{
            lifecycle.withCreated {
                val binding = ViewBindingHelper.inflate<T>(requireContext()).getOrNull()
                if (binding != null) {
                    setContentViewInternal(binding.root)
                    action(binding)
                }
            }
        }
    }
}
