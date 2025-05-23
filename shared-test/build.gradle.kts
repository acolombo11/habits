plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.willbsp.habits.shared.test"
    compileSdk = libs.versions.targetSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.javaVersion.get().toInt())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

dependencies {
    implementation(projects.app)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.espresso.core)
}