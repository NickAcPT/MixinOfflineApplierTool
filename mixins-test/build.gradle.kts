plugins {
    java
}

group = "io.github.nickacpt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven")
    maven("https://maven.fabricmc.net/")
}

val asmVersion = "9.1"
val mixinVersion = "0.9.2+mixin.0.8.2"

dependencies {

    implementation("org.apache.logging.log4j:log4j-core:2.14.0")

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

java {
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_16
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_16
}