import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.kevinfreyap.shared_ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 25

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.constraintlayout)
    api(libs.glide)
    api(libs.material)
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.navigation.ui.ktx)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.swiperefreshlayout)
}