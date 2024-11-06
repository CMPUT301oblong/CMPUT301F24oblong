plugins {
    // alias(libs.plugins.android.application)
    id("com.android.application")
//    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.oblong"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.oblong"
        minSdk = 24
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
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation (libs.zxing.android.embedded)
    implementation (libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
<<<<<<< HEAD
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
=======
>>>>>>> main
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
