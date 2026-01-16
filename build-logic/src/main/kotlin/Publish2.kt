import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

val org.gradle.api.Project.`publishing`: org.gradle.api.publish.PublishingExtension
    get() =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("publishing") as org.gradle.api.publish.PublishingExtension

fun org.gradle.api.Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("publishing", configure)

fun org.gradle.api.Project.mavenPublishing(configure: Action<com.vanniktech.maven.publish.MavenPublishBaseExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure(
        "mavenPublishing",
        configure
    )

fun org.gradle.api.Project.`kotlin`(configure: Action<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlin", configure)


/**
 * Configures the [android][com.android.build.gradle.LibraryExtension]
 * extension.
 */
private fun org.gradle.api.Project.`android`(configure: Action<com.android.build.gradle.LibraryExtension>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("android", configure)

open class Publish2 : Plugin<org.gradle.api.Project> {
    override fun apply(target: Project) {
        target.run {
            if (plugins.hasPlugin("com.android.library")) {
                android {
                    compileSdk = 36
                    defaultConfig {
                        minSdk = 21
                        consumerProguardFiles("consumer-rules.pro")
                    }

                    buildTypes {
                        release {
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                    compileOptions {
                        sourceCompatibility = JavaVersion.VERSION_17
                        targetCompatibility = JavaVersion.VERSION_17
                    }
                    buildFeatures {
                        viewBinding = true
                    }
                    //使用了com.vanniktech.maven.publish插件后不要配置下面的内容否则会报错
//                    publishing {
//                        singleVariant("release") {
//                            withSourcesJar()
//                            withJavadocJar()
//                        }
//                    }
                }
                if (plugins.hasPlugin("com.vanniktech.maven.publish")) {
                    publishing {
                        repositories {
                            maven {
                                name = "MyLocalMaven"
                                url = uri("F:/.m2/repository3")
                            }
                        }

                        mavenPublishing {
                            configure(
                                AndroidSingleVariantLibrary()
                            )
                            publishToMavenCentral()
                            afterEvaluate {
                                coordinates(
                                    groupId = "com.github.knightwood.android.accompanist",
                                    artifactId = project.name,
                                    version = "2.0.0"
                                )
                            }
                            pom {
                                name.set(project.name)
                                description.set("some toolkit library for android")
                            }
                        }
//                        afterEvaluate {
//                            publications {
//                                register<MavenPublication>("maven") {
//                                    groupId = "com.github.knightwood.android.accompanist"
//                                    artifactId = project.name
//                                    version = "2.0.0"
//                                    from(components.getByName("release"))
//                                }
//                            }
//                        }
                    }
                }
            } else {
                println("project: ${project.name} no android plugin")
            }
            kotlin {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }

}
