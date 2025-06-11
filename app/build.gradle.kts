plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}


android {
    namespace = "com.example.mobileanwendungvorlesung"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobileanwendungvorlesung"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation("io.ktor:ktor-client-content-negotiation:3.1.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.3")
    implementation(libs.qrcode.kotlin)
    implementation(libs.core)
    implementation(libs.androidx.navigation.compose.v277)
    implementation(libs.gson)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    // ML Kit Barcode Scanning
    implementation(libs.barcode.scanning)
    // Permissions
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.material3.adaptive.navigation.suite)

    implementation ("androidx.room:room-runtime:2.7.1")
    ksp ("androidx.room:room-compiler:2.7.1")
    implementation (libs.room.ktx)
    implementation (libs.androidx.room.paging)
    implementation(libs.coil.compose)


}