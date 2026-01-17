package com.kiylx.immersionbar

import android.os.Bundle
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import com.kiylx.libx.windowinsinsets.ThemeType
import com.kiylx.libx.windowinsinsets.quickImmersion
import com.kiylx.libx.windowinsinsets.statusBarTheme
import com.kiylx.immersionbar.databinding.ActivityMainBinding


class MainActivity : FragmentActivity() {
    lateinit var content: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = ActivityMainBinding.inflate(layoutInflater)
        setContentView(content.root)
        content.btnDialog.setOnClickListener {
            DialogExampleActivity.start(this)
        }
//        quickImmersion(window.decorView, false) {
//            content.updatePadding(top = it.top)
//        }

        //另一种方式：
        quickImmersion(ignoringVisibility = false) {
            content.root.updatePadding(top = it.top)
        }
        //将状态栏设置为浅色主题，将得到深色的文字和图标
        statusBarTheme(ThemeType.LIGHT)

    }

    companion object {
        const val TAG = "MainActivity"
    }
}
