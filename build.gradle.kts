plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
subprojects {
    configurations.all {
        resolutionStrategy {
            // Force newer JetBrains annotations to avoid conflicts
            force("org.jetbrains:annotations:23.0.0")
        }

        // Exclude old IntelliJ annotations that conflict with JetBrains annotations
        exclude(group = "com.intellij", module = "annotations")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
