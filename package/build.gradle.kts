plugins {
    base
}

val dependents = listOf(
    ":js-lib",
    ":common",
    ":popup",
    ":prefs",
    ":worker",
)

val copyTarget = layout.buildDirectory.dir("dist")
val theCopy by tasks.registering(Copy::class) {
    dependents.forEach { from(project(it).layout.buildDirectory.dir("dist/js/productionExecutable")) }
    from(layout.projectDirectory.dir("src/main/resources"))
    from(layout.buildDirectory.dir("src/main/resources"))
    into(copyTarget)
}

val pack by tasks.registering(Zip::class) {
    from(copyTarget)
}

tasks {
    theCopy {
        dependsOn(
            dependents.map { "$it:jsBrowserDistribution" }
        )
    }

    pack {
        dependsOn(theCopy)
    }

    assemble {
        dependsOn(pack)
    }
}