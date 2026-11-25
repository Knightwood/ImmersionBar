plugins {
    //`kotlin-dsl` 即 id("org.gradle.kotlin.dsl") 用于gradle kts支持
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm) //kotlin-jvm平台支持
    alias(libs.plugins.publish)  //publish插件
}

dependencies {
    //版本需与 KotlinDependencyExtensions.kt 文件中embeddedKotlinVersion 版本一致
    implementation(libs.pluginApi.gradle.kts)
    implementation(libs.vanniktech.publish.plugin)
    implementation(libs.pluginApi.agp)
}

gradlePlugin {
    plugins {
        register("publish2") {
            id = "inner.plugin.publish"
            implementationClass = "Publish2"
        }
    }
}
