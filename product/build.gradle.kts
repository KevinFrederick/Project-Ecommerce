import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.kevinfreyap.product"
    compileSdk = 36

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(project(":core"))
    implementation(project(":shared-wishlist"))
    implementation(project(":shared-cart"))
    implementation(project(":shared-product"))
    implementation(project(":shared-ui"))

    implementation(libs.androidx.viewpager2)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.paging.runtime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}