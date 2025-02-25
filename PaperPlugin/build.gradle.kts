import de.chojo.Repo
import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    alias(libs.plugins.paper.yml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.server)
    alias(libs.plugins.publishdata)
    alias(libs.plugins.publish.hangar)
    alias(libs.plugins.publish.modrinth)
}

dependencies {
    compileOnly(platform(libs.fawe.bom))
    compileOnly(libs.fawe.core)
    compileOnly(libs.fawe.bukkit)
    compileOnly(libs.paper)

    implementation(libs.bstats)
    implementation(libs.commands.cloud.core)
    implementation(libs.commands.cloud.paper)
    implementation(project(":api"))
    implementation(project(":TransferStrategies"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
    runServer {
        minecraftVersion("1.21.4")
    }
    register<RunServer>("runServer2") {
        group = "run paper"
        minecraftVersion("1.21.4")
        runDirectory.set(File("run-2"))
        pluginJars(*project.getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
            .toTypedArray())
    }
    shadowJar {
        relocate("org.bstats", "net.onelitefeather.clipboardconnect.org.bstats")
        relocate("org.redisson", "net.onelitefeather.clipboardconnect.org.redisson")
        relocate("com.fasterxml.jackson", "net.onelitefeather.clipboardconnect.com.fasterxml.jackson")
    }
}

paper {
    main = "net.onelitefeather.clipboardconnect.paper.ClipboardConnect"
    version = "publishData.getVersion(true)"
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
    "1.21",
    "1.21.1",
    "1.21.2",
    "1.21.3",
    "1.21.4",
)

publishData {
    addBuildData()
    addRepo(Repo.main("","",true))
    addRepo(Repo.snapshot("","",true))
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
