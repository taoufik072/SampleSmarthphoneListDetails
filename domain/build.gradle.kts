plugins {
    id("discover.jvm.library")
}

dependencies {

    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(kotlin("test"))
}
