plugins {
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
}

group = project.property("group").toString()

version = project.property("version").toString()

repositories {}

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
    implementation(
        "net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}"
    )
}

tasks.processResources {
    val version = version
    val id = project.property("id").toString()
    val minecraft = project.property("minecraft_version").toString()
    val loader = project.property("loader_version").toString()
    val fabric = project.property("fabric_api_version").toString()

    inputs.properties(
        "version" to version,
        "id" to id,
        "minecraft" to minecraft,
        "loader" to loader,
        "fabric" to fabric,
    )

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "id" to id,
            "minecraft" to minecraft,
            "loader" to loader,
            "fabric" to fabric,
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }
}
