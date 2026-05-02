plugins {
    id("org.sonarqube")
}

sonar {
    properties {
        property("sonar.projectKey", "taoufik072_SampleSmarthphoneListDetails")
        property("sonar.organization", "taoufik072")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/kover/report.xml").get().asFile.absolutePath,
        )
        property(
            "sonar.kotlin.detekt.reportPaths",
            listOf(
                ":app",
                ":presentation",
                ":data",
                ":domain",
                ":common-core",
            ).joinToString(",") { moduleName ->
                "${project(moduleName).layout.buildDirectory.get()}/reports/detekt/detekt.xml"
            },
        )
        property(
            "sonar.sources",
            subprojects
                .flatMap { p ->
                    listOf("${p.projectDir}/src/main/java", "${p.projectDir}/src/main/kotlin")
                }
                .filter { File(it).exists() }
                .joinToString(","),
        )
        property(
            "sonar.tests",
            subprojects
                .flatMap { p ->
                    listOf("${p.projectDir}/src/test", "${p.projectDir}/src/androidTest")
                }
                .filter { File(it).exists() }
                .joinToString(","),
        )

    }
}
