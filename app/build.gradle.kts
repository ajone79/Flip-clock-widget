plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.ajone79.flipclock"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.ajone79.flipclock"
        minSdk = 26
        targetSdk = 36
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
        versionName = "build ${System.getenv("BUILD_NUMBER") ?: "dev"}"
    }

    // A fixed key (committed on purpose, it only signs test builds) so new APKs install over old ones.
    signingConfigs {
        getByName("debug") {
            storeFile = file("flipclock.keystore")
            storePassword = "flipclock"
            keyAlias = "flipclock"
            keyPassword = "flipclock"
        }
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    testOptions { unitTests.isReturnDefaultValues = true }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
