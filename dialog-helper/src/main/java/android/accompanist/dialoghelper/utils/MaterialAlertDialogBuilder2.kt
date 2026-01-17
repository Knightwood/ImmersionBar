package android.accompanist.dialoghelper.utils

import android.content.Context
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.accompanist.dialoghelper.component.ComposeUI

class MaterialAlertDialogBuilder2 : MaterialAlertDialogBuilder {

    constructor(context: Context) : super(
        /* context = */ context,
//        /* overrideThemeResId = */ R.style.BaseFullscreenDialog
    )
    constructor(context: Context, theme: Int) : super(
        /* context = */ context,
        /* overrideThemeResId = */ theme
    )

    /**
     * 链式调用在遇到一些根据条件调用的场景有些不好用，
     * 所以这里提供DSL方式
     */
    fun dsl(action: MaterialAlertDialogBuilder2.() -> Unit): MaterialAlertDialogBuilder2 {
        this.apply(action)
        return this
    }

    fun setComposeUI(ui: ComposeUI): MaterialAlertDialogBuilder2 {
        setView(ComposeView(context).apply {
            setContent {
                ui()
            }
        })
        return this
    }

}
