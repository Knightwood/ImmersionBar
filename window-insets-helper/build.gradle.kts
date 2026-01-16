plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.vanniktech.mavenPublish)
    id("inner.plugin.publish")
}

android {
    namespace = "android.accompanist.insetshelper"
}



dependencies {
    implementation(libs.bundles.bundleAndroidx)
    implementation(libs.bundles.kotlins.android)
    //取色
    implementation("androidx.palette:palette:1.0.0")
}
