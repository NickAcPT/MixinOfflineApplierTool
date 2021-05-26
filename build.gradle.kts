plugins {
    kotlin("jvm") version "1.4.32"
}

group = "io.github.nickacpt"
version = "1.0-SNAPSHOT"

val asmVersion = "9.1"
val mixinVersion = "0.9.2+mixin.0.8.2"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven")
    maven("https://maven.fabricmc.net/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt.clikt:clikt:3.2.0")

    implementation("org.apache.logging.log4j:log4j-core:2.14.0")

    implementation("com.google.code.gson:gson:2.8.6")
    // Guava 21.0+ required for Mixin
    implementation("com.google.guava:guava:30.1-jre")

    // Code modification
    implementation("org.ow2.asm:asm:${asmVersion}")
    implementation("org.ow2.asm:asm-tree:${asmVersion}")
    implementation("org.ow2.asm:asm-analysis:${asmVersion}")
    implementation("org.ow2.asm:asm-util:${asmVersion}")
    implementation("org.ow2.asm:asm-commons:${asmVersion}")

    implementation("net.fabricmc:sponge-mixin:$mixinVersion") {
        exclude(module= "launchwrapper")
        exclude(module= "guava")
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            sourceCompatibility = "11"
            targetCompatibility = "11"
            jvmTarget = "11"
        }
    }
}