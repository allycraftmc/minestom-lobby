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
    maven("https://mvn.allycraft.de/snapshots/") // minestom-perms
    maven("https://repo.hypera.dev/snapshots/") // minestom-perms(luckperms-minestom) and spark-minestom
    maven("https://repo.lucko.me/") // spark-common
    maven("https://oss.sonatype.org/content/repositories/snapshots/") // dependencies of spark-common
}

dependencies {
    implementation("net.minestom:minestom:2025.08.11-1.21.8")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("com.electronwill.night-config:toml:3.8.3")

    implementation("de.allycraft:minestom-perms:1.0-SNAPSHOT")

    // Spark
    implementation("dev.lu15:spark-minestom:1.10-SNAPSHOT")
}

tasks {
    jar {
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