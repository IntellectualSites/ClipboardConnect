pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://eldonexus.de/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}
rootProject.name = "ClipboardConnect"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("worldedit", "com.sk89q.worldedit","worldedit-bukkit").version("7.2.17")

            library("fawe.bom", "com.intellectualsites.bom", "bom-newest").version("1.39")
            library("fawe.core", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").withoutVersion()
            library("fawe.bukkit", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").withoutVersion()

            library("cloud.bom", "cloud.commandframework", "cloud-bom").version("1.8.4")
            library("cloud.kotlin", "cloud.commandframework", "cloud-kotlin-extensions").withoutVersion()
            library("cloud.paper", "cloud.commandframework", "cloud-paper").withoutVersion()
            library("cloud.annotations", "cloud.commandframework", "cloud-annotations").withoutVersion()
            library("cloud.minecraft.extras", "cloud.commandframework", "cloud-minecraft-extras").withoutVersion()

            version("ktor", "2.3.6")
            library("ktor.core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor.cio", "io.ktor", "ktor-client-cio").versionRef("ktor")
            library("ktor.logging", "io.ktor", "ktor-client-logging").versionRef("ktor")
            library("ktor.auth", "io.ktor", "ktor-client-auth").versionRef("ktor")

            library("redis", "org.redisson","redisson").version("3.23.4")

            library("kotlin.test", "org.jetbrains.kotlin", "kotlin-test").withoutVersion()
            library("kotlin.jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").version("2.16.+")
            library("aerogel", "dev.derklaro.aerogel", "aerogel").version("2.1.0")
            library("paper", "io.papermc.paper", "paper-api").version("1.20.2-R0.1-SNAPSHOT")
            library("zstd", "com.github.luben", "zstd-jni").version("1.5.5-11")

            plugin("paper.yml", "net.minecrell.plugin-yml.paper").version("0.6.0")
            plugin("publishdata", "de.chojo.publishdata").version("1.2.5")
            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
            plugin("run.server","xyz.jpenilla.run-paper").version("2.2.0")
            plugin("publish.hangar","io.papermc.hangar-publish-plugin").version("0.0.5")
            plugin("publish.modrinth","com.modrinth.minotaur").version("2.+")
            plugin("changelog", "org.jetbrains.changelog").version("2.2.0")
        }
    }
}

