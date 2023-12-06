import de.chojo.Repo
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "1.9.20"
    alias(libs.plugins.paper.yml)
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.server)
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

    implementation(platform(libs.cloud.bom))
    implementation(libs.cloud.kotlin)
    implementation(libs.cloud.paper)
    implementation(libs.cloud.annotations)
    implementation(libs.cloud.minecraft.extras)

    implementation(libs.aerogel)
    implementation(libs.kotlin.jackson)
    implementation(libs.redis)
    implementation(libs.zstd)
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

publishData {
    addBuildData()
    addRepo(Repo.main("","",true))
    addRepo(Repo.snapshot("","",true))
}

paper {
    main = "net.onelitefeather.clipboardconnect.ClipboardConnect"
    version = publishData.getVersion(true)
    apiVersion = "1.19"
    name = "ClipboardConnect"
    authors = listOf("TheMeinerLP", "OneLiteFeatherNET")

    serverDependencies {
        // During server run time, require LuckPerms, add it to the classpath, and load it before us
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}


