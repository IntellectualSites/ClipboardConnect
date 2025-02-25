plugins {
    `java-library`
}

dependencies {
    compileOnly(platform(libs.fawe.bom))
    compileOnly(libs.fawe.core)
    compileOnly(libs.fawe.bukkit)
    compileOnly(project(":api"))
    compileOnly(libs.paper)

    api(libs.zstd)
    api(libs.redis)
    api(libs.caffeine)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
}