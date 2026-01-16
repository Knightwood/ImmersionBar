package com.kiylx.immersionbar.dialog_example

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.kiylx.customviews.dialog.component.FullScreenDialog

class DialogExampleActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme() {
                Scaffold() { insets ->
                    Column(modifier = Modifier.padding(insets)) {
                        BasicButton("FullScreenDialog1") {
                            val dialog = FullScreenDialog2(this@DialogExampleActivity)
                            dialog.show()
                        }
                        BasicButton("属性测试") {
                            val dialog = FullScreenDialog(this@DialogExampleActivity)
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
