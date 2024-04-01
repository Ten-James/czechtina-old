import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "cz.tenjames"
version = "0.1.6"

repositories {
    mavenCentral()
    maven {
        url = uri("https://j-jzk.cz/dl/maven")
    }
}

dependencies {
    implementation("org.json:json:20230618")
    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.2.0")

    implementation("cz.j_jzk.klang:klang:1.0-rc3")
    implementation("cz.j_jzk.klang:klang-prales:1.0-rc3")
    implementation(project(":czechCommon"))
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