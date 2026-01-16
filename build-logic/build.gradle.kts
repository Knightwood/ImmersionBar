plugins {
    //`kotlin-dsl` 即 id("org.gradle.kotlin.dsl") 用于gradle kts支持
    `kotlin-dsl`
}

dependencies {
    //版本需与 KotlinDependencyExtensions.kt 文件中embeddedKotlinVersion 版本一致
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.35.0")
    implementation("com.android.tools.build:gradle:8.10.0")
}

gradlePlugin {
    plugins {
        register("publish2") {
            id = "inner.plugin.publish"
            implementationClass = "Publish2"
        }
    }
}
