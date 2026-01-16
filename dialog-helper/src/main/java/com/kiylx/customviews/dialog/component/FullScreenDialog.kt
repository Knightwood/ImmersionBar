package com.kiylx.customviews.dialog.component

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.kiylx.customviews.dialog.utils.DialogFullScreenHelper

/**
 * 使用此Dialog,不要传入自定义主题
 */
open class FullScreenDialog(context: Context) : ComponentDialog(context) {

    init {
        DialogFullScreenHelper.setUp(this)
    }

}
