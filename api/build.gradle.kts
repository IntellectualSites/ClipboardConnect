plugins {
    `java-library`
}

dependencies {
    compileOnly(libs.gson)
    compileOnly(platform(libs.fawe.bom))
    compileOnly(libs.fawe.core)
    compileOnly(libs.fawe.bukkit)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }
}