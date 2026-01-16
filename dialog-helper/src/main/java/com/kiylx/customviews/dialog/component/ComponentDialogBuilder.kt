package com.kiylx.customviews.dialog.component

import android.content.Context
import androidx.activity.ComponentDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

/**
 * ```
 * ComposeComponentDialogBuilder()
 *     .configuration {
 *
 *     }
 *     .content {
 *          MaterialTheme {
 *              Column {
 *                  BasicButton(text = "确定") {
 *                      dismiss()
 *                  }
 *              }
 *          }
 *     }
 *     .build(context = context).show()
 * ```
 */
class ComponentDialogBuilder {
    private var contentImpl: (@Composable () -> Unit)? = null
    private var configurationImpl: ComponentDialog.() -> Unit = {}
    private lateinit var dialog: ComponentDialog

     fun configuration(block: ComponentDialog.() -> Unit): ComponentDialogBuilder {
        this.configurationImpl = block
        return this
    }

     fun content(content: @Composable () -> Unit): ComponentDialogBuilder {
        this.contentImpl = content
        return this
    }

     fun build(context: Context, themeResId: Int): ComponentDialogBuilder {
        if (this::dialog.isInitialized) {
            return this
        }
        val dialog = ComponentDialog(context = context, themeResId = themeResId)
        val view = ComposeView(context).apply {
            setContent {
                contentImpl?.invoke()
            }
        }
        dialog.configurationImpl()
        dialog.setContentView(view = view)
        this.dialog = dialog
        return this
    }

     fun show() {
        dialog.show()
    }

     fun dismiss() {
        dialog.dismiss()
    }
}
