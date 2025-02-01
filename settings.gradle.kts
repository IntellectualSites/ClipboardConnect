pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://eldonexus.de/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ClipboardConnect"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("worldedit", "com.sk89q.worldedit","worldedit-bukkit").version("7.3.10")

            library("fawe.bom", "com.intellectualsites.bom", "bom-newest").version("1.52")
            library("fawe.core", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").withoutVersion()
            library("fawe.bukkit", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").withoutVersion()

            version("cloud", "1.8.4")
            library("cloud.paper", "cloud.commandframework", "cloud-paper").versionRef("cloud")
            library("cloud.annotations", "cloud.commandframework", "cloud-annotations").versionRef("cloud")
            library("cloud.minecraft.extras", "cloud.commandframework", "cloud-minecraft-extras").versionRef("cloud")

            library("redis", "org.redisson","redisson").version("3.44.0")

            library("kotlin.test", "org.jetbrains.kotlin", "kotlin-test").withoutVersion()
            library("kotlin.jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").version("2.18.+")
            library("aerogel", "dev.derklaro.aerogel", "aerogel").version("2.1.0")
            library("paper", "io.papermc.paper", "paper-api").version("1.20.2-R0.1-SNAPSHOT")
            library("zstd", "com.github.luben", "zstd-jni").version("1.5.6-8")

            library("bstats", "org.bstats", "bstats-bukkit").version("3.1.0")

            plugin("paper.yml", "net.minecrell.plugin-yml.paper").version("0.6.0")
            plugin("publishdata", "de.chojo.publishdata").version("1.4.0")
            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
            plugin("run.server","xyz.jpenilla.run-paper").version("2.3.1")
            plugin("publish.hangar","io.papermc.hangar-publish-plugin").version("0.1.2")
            plugin("publish.modrinth","com.modrinth.minotaur").version("2.+")
        }
    }
}

