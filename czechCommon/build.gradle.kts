plugins {
    kotlin("jvm") version "1.7.20"
}

group = "cz.tenjames"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}