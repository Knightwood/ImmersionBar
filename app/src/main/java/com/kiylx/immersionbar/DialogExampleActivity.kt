package com.kiylx.immersionbar

import android.accompanist.dialoghelper.component_dialog.backgroundDim
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentDialog
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import android.accompanist.dialoghelper.dialog_fragment.ComposeDialogFragment
import android.accompanist.dialoghelper.material_alert_dialog.dsl
import android.accompanist.dialoghelper.component_dialog.setContentView
import android.accompanist.dialoghelper.material_alert_dialog.setView
import android.accompanist.dialoghelper.material_alert_dialog.setInputView
import android.accompanist.dialoghelper.component_dialog.withBinding
import android.accompanist.dialoghelper.dialog_fragment.ViewDialogFragment
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kiylx.immersionbar.dialog_example.BlurActivity1
import com.kiylx.immersionbar.dialog_example.FullScreenExampleDialog
import com.kiylx.immersionbar.dialog_example.FullScreenExampleDialogFragment
import com.kiylx.immersionbar.dialog_example.TransparentExampleActivity
import com.kiylx.immersionbar.databinding.DialogMainBinding
import com.kiylx.immersionbar.dialog_example.AnimateDialogContent
import com.kiylx.immersionbar.dialog_example.DirectionState
import com.kiylx.immersionbar.dialog_example.TestDialogContent
import com.google.android.material.R as MaterialR

class DialogExampleActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme() {
                val scrollState = rememberScrollState()
                Scaffold() { insets ->
                    Column(
                        modifier = Modifier
                            .padding(insets)
                            .verticalScroll(scrollState)
                    ) {
                        //ComponentDialog测试
                        Text("ComponentDialog")
                        ComponentDialog_FullScreen()
                        ComponentDialog_ComposeAnimVisibility()
                        ComponentDialog_ViewBinding()
                        HorizontalDivider()

                        //MaterialAlertDialog测试
                        Text("MaterialAlertDialog")
                        MaterialAlertDialogComposeUi()
                        MaterialAlertDialogViewBindingBind()
                        MaterialAlertDialogViewBindingInflate()
                        MaterialAlertDialogSetInputView()
//                        MaterialAlertDialogStyles()
                        HorizontalDivider()

                        //DialogFragment
                        Text("DialogFragment")
                        DialogFragment_FullScreen()
                        DialogFragment_setContentView_ComposeUi()
                        DialogFragment_setContentView_ViewBinding()
                        HorizontalDivider()

                        //Activity测试
                        Text("Activity")
                        ArrowItem("透明Activity") {
                            TransparentExampleActivity.Companion.start(this@DialogExampleActivity)
                        }
                        ArrowItem("磨砂Activity") {
                            BlurActivity1.Companion.start(this@DialogExampleActivity)
                        }
                        HorizontalDivider()

                    }
                }
            }
        }
    }

    //<editor-fold desc="ComponentDialog">


    @Composable
    private fun ComponentDialog_FullScreen() {
        ArrowItem(text = "全屏", desc = "使弹窗全屏化") {
            val dialog = FullScreenExampleDialog(this@DialogExampleActivity)
            dialog.show()
        }
    }

    /**
     * 1. 在onCreate方法外调用setContentView，设置compose ui
     * 2. 使用Compose的动画作显示和隐藏
     */
    @Composable
    private fun ComponentDialog_ComposeAnimVisibility() {
        ArrowItem(
            text = "setContentView",
            desc = "1. 在onCreate方法外调用setContentView\n" +
                    "2. 设置compose ui\n" +
                    "3. 使用Compose的动画作显示和隐藏"
        ) {
            //若要用compose中的动画作显示隐藏动画，ComponentDialog所用style不能有动画
            val dialog = ComponentDialog(
                this@DialogExampleActivity,
                R.style.NormalDialogTheme
            )
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.let {
                //启用edge-to-edge
                WindowCompat.enableEdgeToEdge(it)
                //修改窗体属性使内部视图显示在底部
                it.attributes = it.attributes.apply {
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                }
            }
            var visibility by mutableStateOf(false)
            //dialog的setContentView可以不在onCreate中调用
            //只要在show方法调用之前调用就行
            dialog.setContentView {
                /**
                 * 显示和隐藏动画原理：
                 * 1. 调用show方法显示弹窗
                 * 2. 在compose 结构中把visibility修改为true，AnimateDialogContent执行显示
                 * 3. 当visibility修改为false时，AnimateDialogContent执行隐藏，触发onDispose，调用dismiss隐藏弹窗
                 */
                LaunchedEffect(Unit) {
                    visibility = true
                }
                AnimateDialogContent(
                    visible = visibility,
                    onDismissRequest = {
                        dialog.dismiss()
                    },
                    direction = DirectionState.BOTTOM,
                    content = {
                        TestDialogContent(
                            dismiss = {
                                visibility = false
                            },
                            changeDim = {
                                dialog.backgroundDim(it)
                            }
                        )
                    }
                )
            }
            dialog.show()
        }
    }

    @Composable
    private fun ComponentDialog_ViewBinding() {
        ArrowItem(
            text = "setContentView",
            desc = "在onCreate方法外调用setContentView，使用ViewBinding加载布局"
        ) {
            //若要用compose中的动画作显示隐藏动画，ComponentDialog所用style不能有动画
            val dialog = ComponentDialog(this@DialogExampleActivity)
            dialog.setContentView<DialogMainBinding> {
                this.tv1.text = "测试"
            }
            dialog.show()
        }
    }

    //</editor-fold>
    //<editor-fold desc="MaterialAlertDialogBuilder">
    /**
     * MaterialAlertDialog 内置样式选择
     */
    @Composable
    private fun MaterialAlertDialogStyles() {
        Text("MaterialAlertDialog样式")
        SelectMaterialAlertDialogStyle(onClick = { styleId ->
            MaterialAlertDialogBuilder(this@DialogExampleActivity, styleId)
                .dsl {
                    setTitle("标题")
                    setIcon(R.drawable.baseline_wifi_24)
                    setMessage("内容")
                    setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNegativeButton("取消", null)
                }.show()
        })
    }

    /**
     * MaterialAlertDialog 输入框扩展方法
     */
    @Composable
    private fun MaterialAlertDialogSetInputView() {
        ArrowItem(
            text = "输入框",
            desc = "MaterialAlertDialog预定义好的输入框组件"
        ) {
            val dialog = MaterialAlertDialogBuilder(this@DialogExampleActivity)
                .setTitle("标题")
                .setMessage("内容")
                .setInputView {
                    this.textInputText.setText("默认值")
                    this.textInputText.addTextChangedListener {

                    }
                }
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    /**
     * MaterialAlertDialog显示之后动态绑定布局
     */
    @Composable
    private fun MaterialAlertDialogViewBindingBind() {
        ArrowItem(
            text = "ViewBinding测试",
            desc = "先setView传递布局id，显示弹窗后使用ViewBinding绑定布局"
        ) {
            val dialog = MaterialAlertDialogBuilder(this@DialogExampleActivity)
                .setTitle("标题")
                .setMessage("内容")
                .setView(R.layout.dialog_main)
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                }.create()
            dialog.show()
            dialog.withBinding<DialogMainBinding>(
                R.id.dialog_root
            ) {
                tv1.text = "微软"
            }
        }
    }

    /**
     * ViewBinding加载布局并设置给弹窗
     * 使用自定义弹窗主题
     */
    @Composable
    private fun MaterialAlertDialogViewBindingInflate() {
        ArrowItem(
            text = "ViewBinding测试",
            desc = "1. 使用自定义弹窗主题\n" +
                    "2. 调用setView扩展函数，用ViewBinding加载布局"
        ) {
            val dialog = MaterialAlertDialogBuilder(
                this@DialogExampleActivity,
                android.accompanist.dialoghelper.R.style.MaterialAlertDialogStyle1
            )
                .setTitle("标题")
                .setMessage("内容")
                .setView<DialogMainBinding> {
                    tv1.text = "微软"
                }
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    /**
     * MaterialAlertDialog设置compose ui
     */
    @Composable
    private fun MaterialAlertDialogComposeUi() {
        ArrowItem(
            text = "设置composeUI",
            desc = "1. dsl扩展函数\n2. setView函数设置ComposeUI"
        ) {
            MaterialAlertDialogBuilder(this@DialogExampleActivity)
                .dsl {
                    setTitle("标题")
                    setMessage("内容")
                    setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setView {
                        Text("Compose内容")
                    }
                }.create().show()
        }
    }

    //</editor-fold>
    //<editor-fold desc="DialogFragment">
    /**
     * 全屏的DialogFragment
     */
    @Composable
    private fun DialogFragment_FullScreen() {
        ArrowItem(
            text = "DialogFragment全屏",
            desc = "使弹窗全屏"
        ) {
            val dialog = FullScreenExampleDialogFragment()
            dialog.show(supportFragmentManager, "FullScreenDialogFragment")
        }
    }

    /**
     * 实现不继承DialogFragment，new DialogFragment之后直接设置composeUI、Dialog
     */
    @Composable
    private fun DialogFragment_setContentView_ComposeUi() {
        ArrowItem(
            text = "设置composeUI",
            desc = "在不继承DialogFragment重写onCreateView、onCreatDialog的情形下" +
                    "加载ComposeUI视图、替换Dialog"
        ) {
            val dialog = ComposeDialogFragment()
            //可以替换dialog，不用继承和重写
//            dialog.setDialog(ComponentDialog(context))
            //可以替换视图内容，不用继承和重写
            dialog.setContentView {
                Text("Compose内容")
            }
            dialog.show(supportFragmentManager, "ComposeDialogFragment")
        }
    }

    /**
     * 实现不继承DialogFragment，new DialogFragment之后直接设置布局id、viewbinding
     */
    @Composable
    private fun DialogFragment_setContentView_ViewBinding() {
        ArrowItem(
            text = "加载View",
            desc = "在不继承DialogFragment重写onCreateView、onCreatDialog的情形下" +
                    "加载View布局、使用ViewBinding加载布局、替换Dialog"
        ) {
            val dialog = ViewDialogFragment()
            dialog.show(supportFragmentManager, "ComposeDialogFragment")
            //可以使用布局id加载视图
//            dialog.setContentView(R.layout.dialog_main)
            //也可以使用ViewBinding加载视图
            dialog.setContentView<DialogMainBinding> {
                tv1.text = "微软"
            }
        }
    }
    //</editor-fold>


    companion object {
        fun start(activity: Context) {
            val intent = Intent(activity, DialogExampleActivity::class.java)
            activity.startActivity(intent)
        }
    }
}

@Composable
fun SelectMaterialAlertDialogStyle(onClick: (styleId: Int) -> Unit) {
    val menus = remember {
        mutableStateListOf(
            MaterialR.style.MaterialAlertDialog_Material3,
            MaterialR.style.MaterialAlertDialog_Material3_Title_Icon,
            MaterialR.style.MaterialAlertDialog_Material3_Title_Icon_CenterStacked,
            MaterialR.style.MaterialAlertDialog_Material3_Title_Panel,
            MaterialR.style.MaterialAlertDialog_Material3_Title_Panel_CenterStacked,
            MaterialR.style.MaterialAlertDialog_Material3_Title_Text,
            MaterialR.style.MaterialAlertDialog_Material3_Title_Text_CenterStacked,
        )
    }
    val selectedStyleId = remember { mutableIntStateOf(MaterialR.style.MaterialAlertDialog_Material3) }
    Column {
        menus.forEach { styleId ->
            RadioText(
                text = materialAlertDialogStyleName(styleId),
                selected = styleId == selectedStyleId.intValue,
                onClick = {
                    selectedStyleId.intValue = styleId
                    onClick(styleId)
                }
            )
        }
    }
}

fun materialAlertDialogStyleName(styleId: Int): String {
    return when (styleId) {
        MaterialR.style.MaterialAlertDialog_Material3 -> "Material3"
        MaterialR.style.MaterialAlertDialog_Material3_Title_Icon -> "Material3_Title_Icon"
        MaterialR.style.MaterialAlertDialog_Material3_Title_Icon_CenterStacked -> "Material3_Title_Icon_CenterStacked"
        MaterialR.style.MaterialAlertDialog_Material3_Title_Panel -> "Material3_Title_Panel"
        MaterialR.style.MaterialAlertDialog_Material3_Title_Panel_CenterStacked -> "Material3_Title_Panel_CenterStacked"
        MaterialR.style.MaterialAlertDialog_Material3_Title_Text -> "Material3_Title_Text"
        MaterialR.style.MaterialAlertDialog_Material3_Title_Text_CenterStacked -> "Material3_Title_Text_CenterStacked"
        else -> {
            throw IllegalArgumentException("Unknown styleId: $styleId")
        }
    }
}

@Composable
fun RadioText(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@Composable
fun ArrowItem(text: String, desc: String? = null, onClick: () -> Unit) {
    Surface(onClick = onClick) {
        ListItem(
            headlineContent = { Text(text) },
            supportingContent = { Text(desc ?: "") },
            trailingContent = {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = text,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            },
        )
    }
}
