plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "pl.put.szlaki"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.put.szlaki"
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.activity)
    implementation(libs.kotlinx.coroutines.core)

    // HILT
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.dagger)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.lifecycle.service)

    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)

    // OkHttp
    implementation(libs.okhttp)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    // Moshi
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // WROOMBA
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Jackson
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.stax.api)

    // Voyager
    implementation(libs.voyager.hilt)
    implementation(libs.voyager.tab.navigator.android)
    implementation(libs.voyager.transitions)

    // Osmdroid - nawigacja
    implementation(libs.osmdroid.android)

    // Location + Permissions
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)

    // Wykresy
    implementation(libs.compose.m3)

    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
