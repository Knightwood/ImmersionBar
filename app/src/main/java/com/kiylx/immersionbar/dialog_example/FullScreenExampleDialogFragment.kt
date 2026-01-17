package com.kiylx.immersionbar.dialog_example

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import android.accompanist.dialoghelper.component.backgroundDim
import android.accompanist.dialoghelper.fragment.FullScreenDialogFragment

/**
 * 使用DialogFragment构建一个全屏、背景透明的弹窗
 */
class FullScreenExampleDialogFragment : FullScreenDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            //可以调用此方法修改背景透明度
            backgroundDim(0.3f)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                TestDialogContent(
                    dismiss = {
                        dismiss()
                    },
                    changeDim = {
                        dialog?.backgroundDim(it)
                    }
                )
            }
        }
    }
}
