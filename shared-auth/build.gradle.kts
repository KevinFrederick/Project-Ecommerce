import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.kevinfreyap.shared_auth"
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
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":shared-events"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preference)

    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)

    implementation(libs.kotlinx.coroutines.playServices)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}