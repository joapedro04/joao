// A partir do AGP 9 o suporte a Kotlin já vem embutido no plugin Android,
// por isso não aplicamos mais o plugin "org.jetbrains.kotlin.android".
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.trabalhomobil1"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.trabalhomobil1"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        // Gera uma classe de binding para cada layout XML (substitui findViewById).
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
}
