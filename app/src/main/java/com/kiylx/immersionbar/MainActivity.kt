package com.kiylx.immersionbar

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import com.kiylx.libx.windowinsinsets.ThemeType
import com.kiylx.libx.windowinsinsets.quickImmersion
import com.kiylx.libx.windowinsinsets.statusBarTheme

class MainActivity : AppCompatActivity() {
    lateinit var content: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        content=findViewById(android.R.id.content)

//        quickImmersion(window.decorView, false) {
//            content.updatePadding(top = it.top)
//        }

        //另一种方式：
        quickImmersion(ignoringVisibility = false) {
            content.updatePadding(top = it.top)
        }
        //将状态栏设置为浅色主题，将得到深色的文字和图标
        statusBarTheme(ThemeType.LIGHT)

    }

    companion object {
        const val TAG = "MainActivity"
    }
}