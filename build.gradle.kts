import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "com.github.md"
version = "1.0"

repositories {
    mavenCentral()
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(mapOf("Main-Class" to "com.github.md.ApplicationKt"))
    }
}
val http4kVersion = "3.207.0"
dependencies {

    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile("org.http4k:http4k-core:$http4kVersion")
    compile("org.http4k:http4k-format-jackson:$http4kVersion")

    testCompile("org.junit.jupiter:junit-jupiter-engine:5.3.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}