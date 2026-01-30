plugins {
    id("com.android.library")
    id("discover.android.library")
}

android {
    namespace = "fr.taoufikcode.common.core"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}
