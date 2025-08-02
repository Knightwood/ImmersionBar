buildscript {
    val agp_version by extra("8.10.0")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val agp_version = "8.10.0"
    val kotlin_version = "2.1.20"

    id("com.android.application") version agp_version apply false
    id("com.android.library") version agp_version apply false
    id("org.jetbrains.kotlin.android") version kotlin_version apply false
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
 }
