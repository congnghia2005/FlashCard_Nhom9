plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
<<<<<<< HEAD
    id("com.google.gms.google-services")
=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
}

android {
    namespace = "com.example.flashcard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.flashcard"
        minSdk = 24
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
<<<<<<< HEAD
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Thư viện bổ trợ cho Firebase Coroutines (BẮT BUỘC để dùng .await())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    
=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
<<<<<<< HEAD
    // Networking & Utilities
=======
    // Networking
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.6.0")

<<<<<<< HEAD
=======
    // Dòng này bạn đã có để dùng icon Save:
    implementation("androidx.compose.material:material-icons-extended")

    // ... các

>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
<<<<<<< HEAD
}
=======
}
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
