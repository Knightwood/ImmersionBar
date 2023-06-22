package com.kiylx.immersionbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kiylx.libx.windowinsinsets.ImmersionBar
import com.kiylx.libx.windowinsinsets.ImmersionNavBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //使用默认配置沉浸状态栏
        ImmersionBar()
        //使用默认配置沉浸导航栏
        ImmersionNavBar()
    }
}