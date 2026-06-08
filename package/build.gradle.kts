import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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

/** Files shared by every browser build: the compiled JS + static resources, minus
 *  the manifest (each browser gets a tailored one). */
val assembleCommon by tasks.registering(Copy::class) {
    dependsOn(dependents.map { "$it:jsBrowserDistribution" })
    dependents.forEach { from(project(it).layout.buildDirectory.dir("dist/js/productionExecutable")) }
    from(layout.projectDirectory.dir("src/main/resources")) { exclude("manifest.json") }
    from(layout.buildDirectory.dir("src/main/resources")) { exclude("manifest.json") }
    into(layout.buildDirectory.dir("dist/common"))
}

/** The dual-key manifest; each browser build strips it down to its clean form. */
val baseManifestFile = layout.projectDirectory.file("src/main/resources/manifest.json").asFile

val browsers = listOf("chrome", "firefox")

browsers.forEach { browser ->
    val suffix = browser.replaceFirstChar { it.uppercase() }
    val manifestOutFile = layout.buildDirectory.file("manifests/$browser/manifest.json").get().asFile

    // Chrome keeps the MV3 `service_worker` and drops Firefox's `gecko` settings;
    // Firefox keeps the event-page `scripts` and drops `service_worker`. Locals are
    // captured (not the script object) so the task is configuration-cache safe.
    val writeManifest = tasks.register("writeManifest$suffix") {
        // `baseManifestFile` is a script-level val; capture it into a local so the
        // task body doesn't reference the script object (configuration-cache safe).
        val base = baseManifestFile
        val isChrome = browser == "chrome"
        inputs.file(base)
        outputs.file(manifestOutFile)
        doLast {
            @Suppress("UNCHECKED_CAST")
            val manifest = JsonSlurper().parse(base) as MutableMap<String, Any?>
            @Suppress("UNCHECKED_CAST")
            val background = manifest["background"] as MutableMap<String, Any?>
            if (isChrome) {
                background.remove("scripts")
                manifest.remove("browser_specific_settings")
            } else {
                background.remove("service_worker")
            }
            manifestOutFile.parentFile.mkdirs()
            manifestOutFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(manifest)))
        }
    }

    val assembleDist = tasks.register<Copy>("assembleDist$suffix") {
        dependsOn(assembleCommon, writeManifest)
        from(layout.buildDirectory.dir("dist/common"))
        from(manifestOutFile)
        into(layout.buildDirectory.dir("dist/$browser"))
    }

    val pack = tasks.register<Zip>("pack$suffix") {
        dependsOn(assembleDist)
        from(layout.buildDirectory.dir("dist/$browser"))
        archiveAppendix = browser // -> package-<browser>-<version>.zip
    }

    tasks.named("assemble") {
        dependsOn(pack)
    }
}
