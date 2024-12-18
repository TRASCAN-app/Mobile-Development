plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.capstoneproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.capstoneproject"
        minSdk = 30
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    val cameraxVersion = "1.3.0"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("androidx.camera:camera-core:$cameraxVersion")
    implementation ("androidx.camera:camera-camera2:$cameraxVersion")
    implementation ("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation ("androidx.camera:camera-view:$cameraxVersion")
    implementation ("androidx.camera:camera-extensions:$cameraxVersion")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")


}