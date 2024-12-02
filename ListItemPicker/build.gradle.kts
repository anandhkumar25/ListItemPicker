import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    //id("maven-publish")
}

android {
    namespace = "com.anandh.listitempicker"
    compileSdk = 34

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion ="1.5.15"
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-Xuse-ir", "-Xdump-platform-specific-var-lowering-errors")
    }
}

dependencies {
    implementation ("androidx.compose.ui:ui:1.7.5")
    implementation ("androidx.compose.material3:material3:1.3.1") // Make sure the version matches your Material3 setup
    implementation ("androidx.compose.ui:ui-tooling:1.7.5")

    implementation ("androidx.compose.animation:animation:1.7.5")

    implementation ("androidx.compose.foundation:foundation:1.2.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.5")

    implementation ("androidx.compose.runtime:runtime:1.7.5")

    implementation ("androidx.compose.ui:ui:1.7.5")
    implementation ("androidx.compose.ui:ui-util:1.7.5")
}

//publishing {
//    publications {
//        register<MavenPublication>("release") {
//            afterEvaluate {
//                from(components["release"])
//                groupId = "com.github.anandhkumar25"
//                artifactId = "listitempicker-compose"
//                version = "1.0.0"
//            }
//        }
//    }
//}