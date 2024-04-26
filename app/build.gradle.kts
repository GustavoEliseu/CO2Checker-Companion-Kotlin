plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("dagger.hilt.android.plugin")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.gustavo.cocheckercompanionkotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gustavo.cocheckercompanionkotlin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,DEBUGGABLE,NOT-SELF-INSTRUMENTING"
        testInstrumentationRunnerArguments["androidx.benchmark.dryRunMode.enable"] = "true"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }
    buildFeatures {
        buildConfig = true
    }
    configurations.all {
        resolutionStrategy {
            force("com.android.support:support-annotations:23.1.1")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            android.buildFeatures.dataBinding = true
            manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyCZKsJb1JgLqHe9HdUTlbxV2olTB1MwPug"
            buildConfigField("Boolean", "DEBUG_MODE", "false")
            buildConfigField( "String", "ESP_URL", "\"http://192.168.4.1\"")
            buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyCZKsJb1JgLqHe9HdUTlbxV2olTB1MwPug\"")
            buildConfigField ("okhttp3.logging.HttpLoggingInterceptor.Level", "INTERCEPTOR_LEVEL", "okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS")
        }
        debug{
            isMinifyEnabled = false
            isDebuggable = true
            android.buildFeatures.dataBinding = true
            manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyCZKsJb1JgLqHe9HdUTlbxV2olTB1MwPug"
            buildConfigField("Boolean", "DEBUG_MODE", "true")
            buildConfigField("String", "ESP_URL", "\"http://192.168.4.1\"")
            buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyCZKsJb1JgLqHe9HdUTlbxV2olTB1MwPug\"")
            buildConfigField("okhttp3.logging.HttpLoggingInterceptor.Level", "INTERCEPTOR_LEVEL", "okhttp3.logging.HttpLoggingInterceptor.Level.BODY")
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
    relativeProjectPath(":app")
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("androidx.benchmark:benchmark-macro:1.3.0-alpha01")
    implementation("androidx.benchmark:benchmark-macro-junit4:1.2.3")
    implementation("androidx.compose.ui:ui-graphics-android:1.6.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.firebase:firebase-crashlytics:18.2.10")
    implementation("com.google.firebase:firebase-analytics:21.0.0")
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation("com.google.firebase:firebase-auth:21.0.4")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    //material
    implementation("com.google.android.material:material:1.11.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    //OKHTTP3
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Timber dependency
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.moshi:moshi-adapters:1.12.0")

    // MAPS
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}