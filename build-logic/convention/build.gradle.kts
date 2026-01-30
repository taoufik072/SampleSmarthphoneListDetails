plugins {
    `kotlin-dsl`
}
group = "com.taoufikcode.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "discover.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "discover.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("hilt") {
            id = "discover.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("room") {
            id = "discover.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "discover.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
