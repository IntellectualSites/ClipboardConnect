import de.chojo.Repo
import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    kotlin("jvm") version "2.0.21"
    alias(libs.plugins.paper.yml)
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.server)
    alias(libs.plugins.publish.hangar)
    alias(libs.plugins.publish.modrinth)
}

group = "net.onelitefeather"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly(libs.worldedit)
    compileOnly(platform(libs.fawe.bom))
    compileOnly(libs.fawe.core)
    compileOnly(libs.fawe.bukkit)

    implementation(libs.cloud.paper)
    implementation(libs.cloud.annotations)
    implementation(libs.cloud.minecraft.extras)
    paperLibrary(libs.aerogel)
    paperLibrary(libs.zstd)
    paperLibrary(libs.redis)
    implementation(libs.bstats)

    compileOnly(libs.paper)
    testImplementation(libs.kotlin.test)
}

tasks {
    test {
        useJUnitPlatform()
    }
    runServer {
        minecraftVersion("1.20.6")
    }
    register<RunServer>("runServer2") {
        group = "run paper"
        minecraftVersion("1.20.6")
        runDirectory.set(File("run-2"))
        pluginJars(*rootProject.getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
            .toTypedArray())
    }
    shadowJar {
        relocate("org.bstats", "net.onelitefeather.clipboardconnect.org.bstats")
    }
}
kotlin {
    jvmToolchain(21)
}

val supportedMinecraftVersions = listOf(
    "1.19.4",
    "1.20",
    "1.20.1",
    "1.20.2",
    "1.20.3",
    "1.20.4",
    "1.20.5",
    "1.20.6",
)

publishData {
    addBuildData()
    addRepo(Repo.main("","",true))
    addRepo(Repo.snapshot("","",true))
}

paper {
    main = "net.onelitefeather.clipboardconnect.ClipboardConnect"
    loader = "net.onelitefeather.clipboardconnect.ClipboardConnectLoader"
    version = publishData.getVersion(true)
    apiVersion = "1.19"
    name = "ClipboardConnect"
    authors = listOf("TheMeinerLP", "OneLiteFeatherNET")
    generateLibrariesJson = true

    serverDependencies {
        // During server run time, require LuckPerms, add it to the classpath, and load it before us
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    permissions {
        register("clipboardconnect.service.save") {
            this.default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("clipboardconnect.service.load") {
            this.default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("clipboardconnect.command.load") {
            this.default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("clipboardconnect.command.save") {
            this.default = BukkitPluginDescription.Permission.Default.TRUE
        }

        register("clipboardconnect.command.setup") {
            this.default = BukkitPluginDescription.Permission.Default.OP
        }

        register("clipboardconnect.pack.basic") {
            children = listOf(
                "clipboardconnect.service.save",
                "clipboardconnect.service.load",
                "clipboardconnect.command.load",
                "clipboardconnect.command.save",
            )
        }

        register("clipboardconnect.pack.admin") {
            children = listOf(
                "clipboardconnect.pack.basic",
                "clipboardconnect.command.setup",
            )
        }
    }
}


hangarPublish {
    publications.register("ClipboardConnect") {
        version.set(publishData.getVersion(true))
        channel.set(System.getenv("HANGAR_CHANNEL"))
        apiKey.set(System.getenv("HANGAR_SECRET"))
        id.set("ClipboardConnect")

        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions.set(supportedMinecraftVersions)
            }
        }
    }
}
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("i8DhJQqP")
    versionNumber.set(publishData.getVersion(true))
    versionType.set(System.getenv("MODRINTH_CHANNEL"))
    uploadFile.set(tasks.shadowJar as Any)
    gameVersions.addAll(supportedMinecraftVersions)
    loaders.add("paper")
}
