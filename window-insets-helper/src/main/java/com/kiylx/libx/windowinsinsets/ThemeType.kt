package com.kiylx.libx.windowinsinsets

/**
 * 表示主题类型
 * 系统或应用主题标识黑白
 * 状态栏或导航栏黑白主题下文字和图标是相反颜色
 */
enum class ThemeType(val b: Boolean) {
    LIGHT(true),//浅色
    DARK(false),//深色
}