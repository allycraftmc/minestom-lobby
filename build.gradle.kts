plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.0.0"
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
    maven("https://repo.hypera.dev/snapshots/") // LuckPerms Minestom
}

dependencies {
    implementation("net.minestom:minestom:2025.07.30-1.21.8")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("net.kyori:adventure-text-serializer-ansi:4.24.0")
    implementation("com.electronwill.night-config:toml:3.6.0")

    // LuckPerms
    implementation("dev.lu15:luckperms-minestom:5.5-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:3.7.3")
    implementation("com.h2database:h2:2.1.214")
    implementation("redis.clients:jedis:5.2.0")
    implementation("org.postgresql:postgresql:42.7.6")
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
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    run.configure {
        workingDir = file("run/")
    }

    runShadow {
        workingDir = file("run/")
    }
}