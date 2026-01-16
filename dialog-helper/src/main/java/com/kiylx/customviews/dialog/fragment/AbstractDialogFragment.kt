package jetbrains.compose.accompanist.ui.components.dialog

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.core.graphics.drawable.toDrawable

typealias AndroidComposeDialog = AbstractDialogFragment

/**
 * 使用方式：
 *
 * ```
 * class Example1 : AndroidComposeDialog() {
 *     @Composable
 *     override fun Content() {
 *         SealDialogContent(
 *             title = {
 *                 Text(text = "标题")
 *             },
 *             confirmButton = {
 *                 BasicButton(text = "确定") {
 *                     dismiss()
 *                 }
 *             },
 *             cancelButton = {
 *                 BasicTextButton(text = "取消") {
 *                     dismiss()
 *                 }
 *             },
 *         ) {
 *             Text(text = "内容，内容，内容，内容，内容，内容，内容，内容，内容")
 *         }
 *     }
 * }
 * ```
 */
abstract class AbstractDialogFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                //ComposeView中也有个Content()函数，避免同名。
                this@AbstractDialogFragment.CustomContent()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog: Dialog? = dialog
        dialog?.let { dialog_ ->
            if (dialog_.window != null) {
                val window: Window? = dialog_.window
                val lp: WindowManager.LayoutParams = window!!.attributes
                val manager = requireActivity().windowManager
                val d: Display = manager.defaultDisplay
                if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //如果是横屏
                    lp.gravity = Gravity.RIGHT or Gravity.BOTTOM
                    val point = Point()
                    d.getSize(point)
                    lp.width = (0.5 * point.x) as Int
                } else {
                    //竖屏
                    lp.gravity = Gravity.BOTTOM
                    //指定显示大小
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT
                }
                //lp.height = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT

                //window.setDimAmount(0);//dialog周围全透明
                //显示消失动画
                //window.setWindowAnimations(R.style.animate_dialog)
                //让属性设置生效
                window.setAttributes(lp)
                //设置点击外部可以取消对话框
                isCancelable = true



                //设置dialog背景色为透明色 ColorDrawable(Color.TRANSPARENT)
                dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                //设置dialog窗体颜色透明
                dialog?.window?.setDimAmount(0f)
            }
        }
    }

    @Composable
    abstract fun CustomContent()
}
