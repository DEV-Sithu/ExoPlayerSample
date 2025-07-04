plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "mm.exoplayersample.com"
    compileSdk = 35

    defaultConfig {
        applicationId = "mm.exoplayersample.com"
        minSdk = 24
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.media3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val exo_version = "1.7.1"
    implementation("androidx.media3:media3-exoplayer:$exo_version")
    implementation("androidx.media3:media3-exoplayer-dash:$exo_version")
    implementation("androidx.media3:media3-exoplayer-hls:$exo_version")
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:$exo_version")
    implementation("androidx.media3:media3-exoplayer-rtsp:$exo_version")


    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}