// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
<<<<<<< HEAD
    // Thêm dòng này để định nghĩa plugin Google Services

    id("com.google.gms.google-services") version "4.4.2" apply false
=======
>>>>>>> 27d4e2849a9709f1e2be39e4ce2aed2922d414bf
}