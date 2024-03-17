plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gustavo.benchmark"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        create("benchmark1") {
            isDebuggable = true
            manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyCZKsJb1JgLqHe9HdUTlbxV2olTB1MwPug"
            buildConfigField("Boolean", "DEBUG_MODE", "false")
            buildConfigField("String", "ESP_URL", "\"http://192.168.4.1\"")
            buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyCZKsJb1JgLqHe9HdUTlbxV2olTB1MwPug\"")
            buildConfigField("okhttp3.logging.HttpLoggingInterceptor.Level", "INTERCEPTOR_LEVEL", "okhttp3.logging.HttpLoggingInterceptor.Level.BODY")
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.test.uiautomator:uiautomator:2.2.0")
    implementation("androidx.benchmark:benchmark-macro-junit4:1.2.3")

    //OKHTTP3
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark1"
    }
}