plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.compose.compiler)
    id("inner.plugin.publish")
}

android {
    namespace = "android.accompanist.dialoghelper"
}

version = "2.0.1"

dependencies {
    implementation(libs.bundles.bundleAndroidx)
    implementation(libs.bundles.kotlins.android)
    implementation(libs.google.material) {
        exclude("androidx.activity", "activity")
        exclude("androidx.appcompat", "appcompat")
        exclude("androidx.constraintlayout", "constraintlayout")
        exclude("androidx.core", "core")
        exclude("androidx.recyclerview", "recyclerview")
    }
    implementation("com.android.support:support-annotations:28.0.0")
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bom.androidx.ui)
    implementation(libs.bom.androidx.ui.graphics)
    implementation(libs.bom.androidx.ui.tooling.preview)
    implementation(libs.bom.androidx.material3)
    implementation(libs.androidx.navigation.compose)
}
