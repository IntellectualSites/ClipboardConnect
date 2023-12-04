import de.chojo.Repo
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "1.9.20"
    alias(libs.plugins.paper.yml)
    alias(libs.plugins.publishdata)
}

group = "net.onelitefeather"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks {
    test {
        useJUnitPlatform()
    }
}
kotlin {
    jvmToolchain(17)
}

publishData {
    addBuildData()
    addRepo(Repo.main("","",true))
    addRepo(Repo.snapshot("","",true))
}

paper {
    main = "net.onelitefeather.clipoardconnect.ClipboardConnect"
    version = publishData.getVersion(true)
    apiVersion = "1.19"
    authors = listOf("TheMeinerLP", "OneLiteFeatherNET")

    serverDependencies {
        // During server run time, require LuckPerms, add it to the classpath, and load it before us
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

