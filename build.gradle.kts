import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "cz.tenjames"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://j-jzk.cz/dl/maven")
    }
}

dependencies {
    implementation("org.json:json:20230618")
    testImplementation(kotlin("test"))

    implementation("cz.j_jzk.klang:klang:1.0-rc2")
    implementation("cz.j_jzk.klang:klang-prales:1.0-rc2")
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