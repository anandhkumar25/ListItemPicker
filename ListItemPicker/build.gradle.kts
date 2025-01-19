plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compiler.get()//"1.7.5"
    }
    kotlinOptions {
        jvmTarget = "17"
        //freeCompilerArgs += listOf("-Xuse-ir", "-Xdump-platform-specific-var-lowering-errors")
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    implementation(libs.ui)
    implementation(libs.material3) // Make sure the version matches your Material3 setup
    implementation(libs.ui.tooling)

    implementation(libs.androidx.animation)

    implementation(libs.androidx.foundation)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.runtime)

    implementation(libs.androidx.ui.util)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
                groupId = "com.github.anandhkumar25"
                artifactId = "listitempicker-compose"
                version = "1.0.0"
            }
        }
    }
}