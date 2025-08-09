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
    maven("https://repo.hypera.dev/snapshots/") // luckperms-minestom and spark-minestom
    maven("https://repo.lucko.me/") // spark-common
    maven("https://oss.sonatype.org/content/repositories/snapshots/") // dependencies of spark-common
}

dependencies {
    implementation("net.minestom:minestom:2025.07.30-1.21.8")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("net.kyori:adventure-text-serializer-ansi:4.24.0")
    implementation("com.electronwill.night-config:toml:3.6.0")

    // LuckPerms
    implementation("dev.lu15:luckperms-minestom:5.5-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.spongepowered:configurate-hocon:3.7.3")
    implementation("com.h2database:h2:2.1.214")
    implementation("redis.clients:jedis:5.2.0")
    implementation("org.postgresql:postgresql:42.7.6")

    // Spark
    implementation("dev.lu15:spark-minestom:1.10-SNAPSHOT")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "de.allycraft.lobby.Main"
        }

        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveVersion.set("")
    }

    run.configure {
        workingDir = file("run/")
    }

    runShadow {
        workingDir = file("run/")
    }
}