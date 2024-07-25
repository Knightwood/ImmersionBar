plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 34
    namespace = "com.kiylx.immersionbar"

    defaultConfig {
        applicationId = "com.kiylx.immersionbar"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation(project(path = ":libx"))
}
