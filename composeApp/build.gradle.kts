import org.jetbrains.compose.ExperimentalComposeLibrary
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.add("-Xjdk-release=${JavaVersion.VERSION_17}")
                }
            }
        }
    }


    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            export(libs.kmpnotifier)
            export(libs.koin.core)
            baseName = "ComposeApp"
            isStatic = true
            binaryOptions["bundleId"] = "skdev.omsrings.mobile.ios"
        }
    }

    sourceSets.all {
        languageSettings {
            optIn("kotlin.ExperimentalUuidApi") // Добавьте эту строку
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json.v160)

            // Notifier
            api(libs.kmpnotifier)

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // Navigation
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.screenmodel)

            // Logging
            implementation(libs.napier)

            // Kotlinx features
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            // Settings
            implementation(libs.multiplatformSettings)
            implementation(libs.multiplatformSettings.coroutines)
            implementation(libs.multiplatformSettings.serialization)

            // Dependency injection
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Firebase KMP
            api(libs.gitlive.firebase.auth)
            api(libs.gitlive.firebase.firestore)
//          api(libs.gitlive.firebase.messaging)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)

            // Activity compose
            implementation(libs.androidx.activityCompose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.android)

            // Dependency injection
            implementation(libs.koin.android)

            // Settings
            implementation(libs.multiplatformSettings.datastore)
            implementation(libs.androidx.datastorePreferences)

            // Firebase Android
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.auth.android)
            implementation(libs.firebase.firestore.android)
//          implementation(libs.firebase.messaging.android)
            implementation(libs.firebase.messaging.android)

            // Preview
            implementation(compose.preview)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}

android {
    namespace = "skdev.omsrings.mobile"
    compileSdk = 34

//    val keystorePropertiesFile = rootProject.file("signing/keystore.properties")
//    val keystoreProperties = Properties()
//    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

    signingConfigs {
//        create("release") {
////            keyAlias = keystoreProperties["keyAliasRelease"] as String
////            keyPassword = keystoreProperties["keyPasswordRelease"] as String
////            storeFile = rootProject.file("signing/keystore.jks")
////            storePassword = keystoreProperties["storePassword"] as String
//        }

        getByName("debug") {
//            keyAlias = keystoreProperties["keyAliasDebug"] as String
//            keyPassword = keystoreProperties["keyPasswordDebug"] as String
//            storeFile = rootProject.file("signing/keystore.jks")
//            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
//        release {
//            isMinifyEnabled = false
//            isDebuggable = false
//            signingConfig = signingConfigs.getByName("release")
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }

        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "skdev.omsrings.mobile.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        //enables a Compose tooling support in the AndroidStudio
        compose = true
    }
}
dependencies {
    implementation(libs.firebase.messaging.ktx)
}

buildConfig {
}


