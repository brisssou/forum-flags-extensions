plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        // Browser target produces the distribution bundle consumed by :package.
        browser {
            // No launchable browser in this/CI environment; tests run on Node.
            testTask { enabled = false }
        }
        // Node runs the pure-logic parser tests (linkedom works headless).
        nodejs()
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(npm("linkedom", "0.18.12"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}