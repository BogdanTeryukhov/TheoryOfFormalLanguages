import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "org.mts"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    testImplementation(kotlin("test"))
    implementation("tools.aqua:z3-turnkey:4.12.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}