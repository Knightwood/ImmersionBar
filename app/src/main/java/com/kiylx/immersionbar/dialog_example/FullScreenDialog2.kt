package com.kiylx.immersionbar.dialog_example

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.kiylx.customviews.dialog.component.FullScreenDialog
import com.kiylx.customviews.dialog.utils.DialogFullScreenHelper

class FullScreenDialog2(context: Context) : FullScreenDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val root = ComposeView(context).apply {
            setContent {
                MaterialTheme() {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        TestDialogContent {
                            dismiss()
                        }
                    }
                }
            }
        }
        setContentView(root, lp)
    }
}

@Composable
fun TestDialogContent(dismiss: () -> Unit) {
    Surface(
        modifier = Modifier.sizeIn(maxWidth = 360.dp, maxHeight = 480.dp),
        color = MaterialTheme.colorScheme.tertiary
    ) {
        Column() {
            Text(text = "内容，内容，内容，内容，内容，内容，内容，内容，内容")
            Button(onClick = {
                dismiss()
            }) {
                Text(text = "确定")
            }
        }
    }
}
