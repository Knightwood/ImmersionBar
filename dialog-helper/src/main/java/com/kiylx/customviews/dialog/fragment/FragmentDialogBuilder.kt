package com.kiylx.customviews.dialog.fragment

import androidx.compose.runtime.Composable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import jetbrains.compose.accompanist.ui.components.dialog.AbstractDialogFragment

/**
 * ComposeDialogBuilder
 *
 * 使用DialogFragment承载compose函数，方便 在一些需要展示弹窗的点击函数中使用
 *
 * ```
 * ComposeDialogBuilder().content {
 *     MaterialTheme {
 *         Column {
 *             BasicButton(text = "确定") {
 *                 dismiss()
 *             }
 *         }
 *     }
 * }.build(activity, "AndroidDialog2").show()
 *
 * ```
 *
 * @constructor Create empty ComposeDialogBuilder
 */
class FragmentDialogBuilder  {
    private var contentImpl: (@Composable AbstractDialogFragment. () -> Unit)? = null
    private var configurationImpl: AbstractDialogFragment.() -> Unit = {}
    private lateinit var dialog: DialogFragment
    private var tag: String? = null
    private var fm: FragmentManager? = null


    class Dialog : AbstractDialogFragment() {
        var contentImpl: (@Composable AbstractDialogFragment. () -> Unit)? = null

        @Composable
        override fun CustomContent() {
            contentImpl?.invoke(this)
        }
    }

     fun configuration(block: AbstractDialogFragment.() -> Unit): FragmentDialogBuilder {
        this.configurationImpl = block
        return this
    }

     fun content(content: @Composable AbstractDialogFragment. () -> Unit): FragmentDialogBuilder {
        this.contentImpl = content
        return this
    }

     fun build(fragment: Fragment, tag: String?): FragmentDialogBuilder {
        build(fragment.childFragmentManager, tag)
        return this
    }

     fun build(activity: FragmentActivity, tag: String?): FragmentDialogBuilder {
        build(activity.supportFragmentManager, tag)
        return this
    }

     fun build(fm: FragmentManager, tag: String?): FragmentDialogBuilder {
        if (this::dialog.isInitialized) {
            return this
        }

        val dialog = Dialog().apply {
            configurationImpl()
            this.contentImpl = this@FragmentDialogBuilder.contentImpl
        }
        this.dialog = dialog
        this.fm = fm
        this.tag = tag
        return this
    }

     fun show() {
        dialog.show(fm!!, tag)
    }

     fun dismiss() {
        dialog.dismiss()
    }
}
