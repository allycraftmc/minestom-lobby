plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.0.0-rc2"
}

group = "de.allycraft"
version = "1.0-SNAPSHOT"

application {
    mainClass = "de.allycraft.lobby.Main"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom:2025.07.30-1.21.8")
    implementation("org.slf4j:slf4j-simple:2.0.17")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "de.allycraft.lobby.Main"
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }
}