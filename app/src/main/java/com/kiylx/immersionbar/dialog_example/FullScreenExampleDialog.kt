package com.kiylx.immersionbar.dialog_example

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.accompanist.dialoghelper.component_dialog.FullScreenDialog
import android.accompanist.dialoghelper.component_dialog.backgroundDim
import android.accompanist.dialoghelper.component_dialog.setContentView
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

private const val defaultDim = 0.2f

class FullScreenExampleDialog(context: Context) : FullScreenDialog(context) {
    private val d = this

    init {
        //增加暗色遮罩
        d.backgroundDim(defaultDim)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //允许按返回键关闭弹窗
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setContentView(
//            lp = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            ),// 测试点击R.id.content、或者ComposeView布局区域触发取消弹窗
        ) {
            TestDialogContent(
                dismiss = {
                    d.dismiss()
                },
                changeDim = {
                    d.backgroundDim(it)
                }
            )
        }
    }
}

enum class DirectionState {
    TOP,
    LEFT,
    RIGHT,
    BOTTOM,
    NONE
}

@Composable
fun AnimateDialogContent(
    visible: Boolean = true,
    onDismissRequest: () -> Unit,
    direction: DirectionState = DirectionState.NONE,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier,
        visible = visible,
        enter = when (direction) {
            DirectionState.TOP -> slideInVertically(initialOffsetY = { -it })
            DirectionState.LEFT -> slideInHorizontally(initialOffsetX = { -it })
            DirectionState.RIGHT -> slideInHorizontally(initialOffsetX = { it })
            DirectionState.BOTTOM -> slideInVertically(initialOffsetY = { it })
            else -> fadeIn()
        },
        exit = when (direction) {
            DirectionState.TOP -> fadeOut() + slideOutVertically(targetOffsetY = { -it })
            DirectionState.LEFT -> fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
            DirectionState.RIGHT -> fadeOut() + slideOutHorizontally(targetOffsetX = { it })
            DirectionState.BOTTOM -> fadeOut() + slideOutVertically(targetOffsetY = { it })
            else -> fadeOut()
        }
    ) {//1
        //当执行隐藏，动画完成后，注释1处的内容会从ComposeNode树中消失，DisposableEffect会自动触发，
        //当dispose时意味着动画完成，内容也从屏幕上消失，此时才能隐藏弹窗
        DisposableEffect(Unit) {
            onDispose(onDismissRequest)
        }
        content()
    }
}

@Composable
fun TestDialogContent(
    modifier: Modifier = Modifier,
    dismiss: () -> Unit,
    changeDim: (dim: Float) -> Unit,
) {
    var v by remember() {
        mutableFloatStateOf(defaultDim)
    }
    MaterialTheme() {
        Surface(
            modifier = modifier
                .wrapContentSize(),//使用此修饰符，测试点击setContentView设置的根视图区域触发取消弹窗
//            ,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Surface(
                modifier = Modifier
                    .systemBarsPadding()
                    .requiredHeight(480.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = MaterialTheme.shapes.large,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "改变背景透明度")
                    Slider(
                        value = v,
                        onValueChange = {
                            v = it
                        },
                        onValueChangeFinished = {
                            changeDim(v)
                        }
                    )

                    Button(onClick = {
                        dismiss()
                    }) {
                        Text(text = "确定")
                    }
                }
            }
        }
    }
}
