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
import androidx.compose.material3.Button
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
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
                        FullScreenDialog1()
                        FullScreenDialogFragment()
                        MaterialAlertDialogComposeUi()
                        ViewBindingTest()
                        ViewBindingTest2()
                        DialogSetInputView()
                        DialogFragmentComposeUi()
                        loadView()
                        BasicButton("透明Activity") {
                            TransparentExampleActivity.Companion.start(this@DialogExampleActivity)
                        }
                        BasicButton("磨砂Activity") {
                            BlurActivity1.Companion.start(this@DialogExampleActivity)
                        }
                        setContentViewNotInOnCreate()
//                        HorizontalDivider()
//                        MaterialAlertDialogStyles()
                    }
                }
            }
        }
    }
    //<editor-fold desc="ComponentDialog">
    /**
     * 在onCreate方法外调用setContentView
     */
    @Composable
    private fun setContentViewNotInOnCreate() {
        val scope = rememberCoroutineScope()
        var visible by remember { mutableStateOf(false) }
        BasicButton("ComponentDialog setContentView") {
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
            //dialog的setContentView可以不在onCreate中调用
            //只要在show方法调用之前调用就行
            dialog.setContentView {
                AnimateDialogContent(visible, DirectionState.BOTTOM, {
                    TestDialogContent(
                        dismiss = {
                            scope.launch {
                                visible = false
                                delay(100)
                                dialog.dismiss()
                            }
                        },
                        changeDim = {
                            dialog.backgroundDim(it)
                        }
                    )
                })
            }
            scope.launch {
                dialog.show()
                delay(100)
                visible = true
            }
        }
    }

    @Composable
    private fun FullScreenDialog1() {
        BasicButton("FullScreenDialog1") {
            val dialog = FullScreenExampleDialog(this@DialogExampleActivity)
            dialog.show()
        }
    }

    //</editor-fold>
    //<editor-fold desc="MaterialAlertDialogBuilder">
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

    @Composable
    private fun DialogSetInputView() {
        BasicButton("输入框Dialog") {
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

    @Composable
    private fun ViewBindingTest() {
        BasicButton("MaterialAlertDialogViewBinding测试1") {
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
     * 直接使用ViewBinding加载布局并设置给弹窗
     * 使用自定义弹窗主题
     */
    @Composable
    private fun ViewBindingTest2() {
        BasicButton("MaterialAlertDialogViewBinding测试2") {
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

    @Composable
    private fun MaterialAlertDialogComposeUi() {
        BasicButton("MaterialAlertDialog") {
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
    private fun FullScreenDialogFragment() {
        BasicButton("FullScreenDialogFragment") {
            val dialog = FullScreenExampleDialogFragment()
            dialog.show(supportFragmentManager, "FullScreenDialogFragment")
        }
    }

    /**
     * 不用继承DialogFragment，
     * new DialogFragment之后直接设置composeUI
     */
    @Composable
    private fun DialogFragmentComposeUi() {
        BasicButton("设置composeUI") {
            val dialog = ComposeDialogFragment()
            dialog.setContentView {
                Text("Compose内容")
            }
            dialog.show(supportFragmentManager, "ComposeDialogFragment")
        }
    }

    @Composable
    private fun loadView() {
        BasicButton("DialogFragment加载View") {
            val dialog = ViewDialogFragment()
            dialog.show(supportFragmentManager, "ComposeDialogFragment")
//            dialog.setContentView(R.layout.dialog_main)
            dialog.setContentView<DialogMainBinding> {
                it.tv1.text = "微软"
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
fun BasicButton(text: String, onClick: () -> Unit) {
    Button(content = { Text(text) }, onClick = onClick)
}
