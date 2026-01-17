package com.kiylx.immersionbar

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
import android.accompanist.dialoghelper.component.setContentView
import android.accompanist.dialoghelper.utils.MaterialAlertDialogBuilder2
import android.accompanist.dialoghelper.utils.withBinding
import com.kiylx.immersionbar.dialog_example.BlurActivity1
import com.kiylx.immersionbar.dialog_example.FullScreenExampleDialog
import com.kiylx.immersionbar.dialog_example.FullScreenExampleDialogFragment
import com.kiylx.immersionbar.dialog_example.TransparentExampleActivity
import com.kiylx.immersionbar.databinding.DialogMainBinding

class DialogExampleActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme() {
                Scaffold() { insets ->
                    Column(modifier = Modifier.padding(insets)) {
                        BasicButton("FullScreenDialog1") {
                            val dialog = FullScreenExampleDialog(this@DialogExampleActivity)
                            dialog.show()
                        }
                        BasicButton("FullScreenDialogFragment") {
                            val dialog = FullScreenExampleDialogFragment()
                            dialog.show(supportFragmentManager, "FullScreenDialogFragment")
                        }
                        BasicButton("MaterialAlertDialog") {
                            MaterialAlertDialogBuilder2(this@DialogExampleActivity)
                                .dsl {
                                    setTitle("标题")
                                    setMessage("内容")
                                    setPositiveButton("确定") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    setComposeUI {
                                        Text("Compose内容")
                                    }
                                }.create().show()
                        }

                        BasicButton("Dialog view binding测试") {
                            val dialog = MaterialAlertDialogBuilder2(this@DialogExampleActivity)
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
                        BasicButton("透明Activity") {
                            TransparentExampleActivity.Companion.start(this@DialogExampleActivity)
                        }
                        BasicButton("磨砂Activity") {
                            BlurActivity1.Companion.start(this@DialogExampleActivity)
                        }
                        BasicButton("？？？") {
                            val dialog = ComponentDialog(this@DialogExampleActivity)
                            //dialog的setContentView可以不在onCreate中调用
                            //只要在show方法调用之前调用就行
                            dialog.setContentView {
                                Text("？？？")
                            }
                            dialog.show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun start(activity: Context) {
            val intent = Intent(activity, DialogExampleActivity::class.java)
            activity.startActivity(intent)
        }
    }
}

@Composable
fun BasicButton(text: String, onClick: () -> Unit) {
    Button(content = { Text(text) }, onClick = onClick)
}
