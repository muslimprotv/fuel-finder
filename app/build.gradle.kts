plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.fuelfinder.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fuelfinder.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "4.0-real-navigation"
        multiDexEnabled = true
        resourceConfigurations += listOf("en")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes += setOf("META-INF/DEPENDENCIES", "META-INF/LICENSE*", "META-INF/NOTICE*")
    }
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {
    implementation("com.google.android.libraries.navigation:navigation:7.8.0")
    implementation("com.google.android.libraries.places:places:5.3.0") {
        exclude(group = "com.google.android.gms", module = "play-services-maps")
    }
    implementation("com.google.android.gms:play-services-location:21.4.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_nio:2.1.5")
}
