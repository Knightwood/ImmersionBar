plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 36
    namespace = "com.kiylx.immersionbar"

    defaultConfig {
        applicationId = "com.kiylx.immersionbar"
        minSdk = 26
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"))
        }
    }
    buildFeatures {
        viewBinding = true
        //dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

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

    implementation(project(path = ":window-insets-helper"))
    implementation(project(path = ":dialog-helper"))


    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bom.androidx.ui)
    implementation(libs.bom.androidx.ui.graphics)
    implementation(libs.bom.androidx.ui.tooling.preview)
    implementation(libs.bom.androidx.material3)
    implementation(libs.androidx.navigation.compose)
}
