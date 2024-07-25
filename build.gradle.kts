buildscript {
    val agp_version by extra("7.4.2")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("com.android.library") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
 }
