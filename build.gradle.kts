import org.gradle.api.file.FileCollection
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    id("fabric-loom") version "1.8.9"
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta4" // Shadow plugin
}

version = "${project.property("mod_version")}+mc${project.property("minecraft_version")}" as String
group = project.property("maven_group") as String

base {
    archivesName.set("${project.property("archives_base_name")}")
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("simplespotifycontroller") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}
val library: Configuration by configurations.creating
configurations {

    // include libraries
    implementation.configure {
        extendsFrom(library)
    }
    shadow.configure {
        extendsFrom(library)
    }
}
repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    // Regular dependencies (won't be shadowed)
    library("com.adamratzman:spotify-api-kotlin-core:4.1.3")
    library("org.kotlincrypto:secure-random:0.3.2")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")



}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
    filesMatching("SimpleSpotifyControllerClient.kt") {
        expand(
            "mod_version" to project.version
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}
tasks.shadowJar {
    // only god can save this project now
    from(sourceSets.main.get().output)
    from(sourceSets.getByName("client").output)
    configurations = listOf(project.configurations.shadow.get())


    // Merge service files (if any)
    mergeServiceFiles()
}
tasks.jar {

    from("LICENSE") {
        rename { "${it}_${archiveBaseName.get()}" }
    }
    dependsOn(tasks.shadowJar)
}

// Configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
            artifact(tasks.shadowJar)  // Use shadowJar artifact for publishing
        }
    }

    repositories {
        // Add repositories for publishing your artifact (e.g., Maven Central, local repository, etc.)
    }
}
