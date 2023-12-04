pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://eldonexus.de/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "ClipboardConnect"
include("Paper")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("paper.yml", "net.minecrell.plugin-yml.paper").version("0.6.0")
            plugin("publishdata", "de.chojo.publishdata").version("1.2.5")
        }
    }
}

