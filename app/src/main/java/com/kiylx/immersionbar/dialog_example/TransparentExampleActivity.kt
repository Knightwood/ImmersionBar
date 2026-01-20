package com.kiylx.immersionbar.dialog_example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.accompanist.dialoghelper.activity.TransparentActivityHelper
import android.accompanist.dialoghelper.component_dialog.backgroundDim
import android.accompanist.dialoghelper.utils.backgroundDim

class TransparentExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        TransparentActivityHelper.setUp(this)
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                TestDialogContent(
                    modifier=Modifier.align(Alignment.Center),
                    dismiss = ::finish,
                    changeDim = {
                        window.backgroundDim(it)
                    }
                )
            }
        }
    }

    companion object {
        fun start(activity: Context) {
            val intent = Intent(activity, TransparentExampleActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
