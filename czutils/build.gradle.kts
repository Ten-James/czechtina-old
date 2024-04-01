plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "cz.tenjames"
version = "0.0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://j-jzk.cz/dl/maven")
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":czechCommon"))
    implementation(project(":czechtina"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}