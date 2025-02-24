plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.patelnia8"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.patelnia8"
        minSdk = 25
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
}

dependencies {

    implementation("io.coil-kt:coil:2.2.2") // Podstawowa obsługa obrazów
    implementation("io.coil-kt:coil-gif:2.2.2") // Obsługa GIF-ów

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}