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
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
    }
    versionCatalogs {
        create("libs") {

            version("cloud", "1.8.4")
            library("cloud.paper", "cloud.commandframework", "cloud-paper").versionRef("cloud")
            library("cloud.annotations", "cloud.commandframework", "cloud-annotations").versionRef("cloud")
            library("cloud.minecraft.extras", "cloud.commandframework", "cloud-minecraft-extras").versionRef("cloud")


            library("kotlin.test", "org.jetbrains.kotlin", "kotlin-test").withoutVersion()
            library("kotlin.jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").version("2.18.+")
            library("aerogel", "dev.derklaro.aerogel", "aerogel").version("2.1.0")


            library("worldedit", "com.sk89q.worldedit","worldedit-bukkit").version("7.3.9")
            library("fawe.bom", "com.intellectualsites.bom", "bom-newest").version("1.52")
            library("fawe.core", "com.fastasyncworldedit", "FastAsyncWorldEdit-Core").withoutVersion()
            library("fawe.bukkit", "com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit").withoutVersion()
            library("redis", "org.redisson","redisson").version("3.44.0")
            library("paper", "io.papermc.paper", "paper-api").version("1.21.4-R0.1-SNAPSHOT")
            library("bstats", "org.bstats", "bstats-bukkit").version("3.1.0")
            library("zstd", "com.github.luben", "zstd-jni").version("1.5.6-8")
            library("snakeyaml", "org.yaml", "snakeyaml").version("2.4")
            library("caffeine", "com.github.ben-manes.caffeine", "caffeine").version("3.2.0")
            library("commands.cloud.core", "org.incendo", "cloud-core").version("2.0.0")
            library("commands.cloud.paper", "org.incendo", "cloud-paper").version("2.0.0-beta.10")
            library("commands.cloud.minecraft.extras", "org.incendo", "cloud-minecraft-extras").version("2.0.0-beta.10")
            library("gson", "com.google.code.gson", "gson").version("2.12.1")

            plugin("paper.yml", "net.minecrell.plugin-yml.paper").version("0.6.0")
            plugin("publishdata", "de.chojo.publishdata").version("1.4.0")
            plugin("shadow", "com.gradleup.shadow").version("9.0.0-beta9")
            plugin("run.server","xyz.jpenilla.run-paper").version("2.3.1")
            plugin("publish.hangar","io.papermc.hangar-publish-plugin").version("0.1.2")
            plugin("publish.modrinth","com.modrinth.minotaur").version("2.+")
        }
    }
}
include("api")
include("TransferStrategies")
include("PaperPlugin")
