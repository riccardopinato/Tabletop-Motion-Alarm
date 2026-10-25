// TARGET: app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.riccardopinato.tabletopalarm"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.riccardopinato.tabletopalarm"
        minSdk = 26
        targetSdk = 37
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        val keyPath = providers.environmentVariable("ANDROID_KEYSTORE_PATH").orNull
        val storePasswordValue = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
        val keyAliasValue = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
        val keyPasswordValue = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull

        if (!keyPath.isNullOrBlank() && !storePasswordValue.isNullOrBlank() &&
            !keyAliasValue.isNullOrBlank() && !keyPasswordValue.isNullOrBlank()) {
            create("release") {
                storeFile = file(keyPath)
                storePassword = storePasswordValue
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        checkReleaseBuilds = true
        abortOnError = true
        disable += "FullBackupContent"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.08.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.fragment:fragment-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.navigation:navigation-compose:2.10.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.biometric:biometric:1.1.0")

    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.2.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
