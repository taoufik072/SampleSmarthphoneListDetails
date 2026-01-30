import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    
    commonExtension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    tasks.withType<KotlinCompile>().configureEach {

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose-compiler").get().toString()
        }
    }
}

internal fun Project.configureDependencies(includeCompose: Boolean, isApplication: Boolean) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    
    dependencies {
        add("implementation", libs.findLibrary("androidx.core.ktx").get())
        
        if (isApplication) {
            add("implementation", libs.findLibrary("androidx.lifecycle.runtime.ktx").get())
        }
        
        if (includeCompose) {
            val bom = libs.findLibrary("compose.bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            
            add("implementation", libs.findBundle("compose").get())
            
            add("debugImplementation", libs.findLibrary("compose.ui.tooling").get())
            add("debugImplementation", libs.findLibrary("compose.ui.test.manifest").get())
            
            add("debugImplementation", libs.findLibrary("compose.ui.test.manifest").get())
        }
        
        add("testImplementation", libs.findLibrary("junit").get())
    }
}
