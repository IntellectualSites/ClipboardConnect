import de.chojo.Repo
import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "1.9.21"
    alias(libs.plugins.paper.yml)
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.server)
    alias(libs.plugins.publish.hangar)
    alias(libs.plugins.publish.modrinth)
    alias(libs.plugins.changelog)
}

group = "net.onelitefeather"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly(libs.worldedit)

    paperLibrary(platform(libs.cloud.bom))
    paperLibrary(libs.cloud.paper)
    paperLibrary(libs.cloud.annotations)
    paperLibrary(libs.cloud.minecraft.extras)

    paperLibrary(libs.aerogel)
    paperLibrary(libs.kotlin.jackson)
    paperLibrary(libs.redis)
    paperLibrary(libs.zstd)
    compileOnly(libs.paper)
    testImplementation(libs.kotlin.test)
}

tasks {
    test {
        useJUnitPlatform()
    }
    runServer {
        minecraftVersion("1.20.2")
    }
}
kotlin {
    jvmToolchain(17)
}

val supportedMinecraftVersions = listOf(
    "1.19",
    "1.19.1",
    "1.19.2",
    "1.19.3",
    "1.19.4",
    "1.20",
    "1.20.1",
    "1.20.2",
    "1.20.3",
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

changelog {
    version.set(publishData.getVersion(false))
    path.set("${project.projectDir}/CHANGELOG.md")
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}


hangarPublish {
    publications.register("ClipboardConnect") {
        version.set(project.version.toString())
        channel.set(System.getenv("HANGAR_CHANNEL"))
        changelog.set(
            project.changelog.renderItem(
                project.changelog.getOrNull(publishData.getVersion(false)) ?: project.changelog.getUnreleased()
            )
        )
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
    versionNumber.set(version.toString())
    versionType.set(System.getenv("MODRINTH_CHANNEL"))
    uploadFile.set(tasks.shadowJar as Any)
    gameVersions.addAll(supportedMinecraftVersions)
    loaders.add("paper")
    loaders.add("bukkit")
    changelog.set(
        project.changelog.renderItem(
            project.changelog.getOrNull(publishData.getVersion(false)) ?: project.changelog.getUnreleased()
        )
    )
}