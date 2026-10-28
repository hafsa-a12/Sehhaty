import java.util.Properties

plugins {
    id("com.android.application")
}

// Secrets / config lus depuis local.properties (non versionné) ou variables d'environnement (CI)
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun prop(name: String, default: String = ""): String =
    localProps.getProperty(name) ?: System.getenv(name) ?: default

android {
    namespace = "com.sante.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sante.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"

        // Clé Maps injectée dans le manifest (jamais en dur dans le code)
        manifestPlaceholders["MAPS_API_KEY"] = prop("MAPS_API_KEY")
        // URL du backend injectée dans BuildConfig
        buildConfigField("String", "API_BASE_URL", "\"${prop("API_BASE_URL", "http://10.0.2.2:3000/api/")}\"")
    }

    buildFeatures {
        buildConfig = true
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
        encoding = "UTF-8"
    }
}

dependencies {
    // UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Maps & Location
    implementation(libs.maps)
    implementation(libs.location)

    // Réseau
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)

    // Architecture : MVVM (ViewModel + LiveData) + Room
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

}
