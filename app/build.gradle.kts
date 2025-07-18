plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.odootest"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.odootest"
        minSdk = 26
        targetSdk = 36
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
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)

    // RxJava3 và Adapter cho Retrofit
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.retrofit2.adapter.rxjava3)

    // ViewModel & LiveData (for MVVM)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    // Android Keystore (for Secure Storage)
    implementation(libs.androidx.security.crypto)
}