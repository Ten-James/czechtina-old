package utils

import java.io.File

class BuildFile( val Output: String, val buildDir: String, val main: String, val src: String) {

}

fun readBuildFile(): BuildFile {
    val file = File(".buildczech")
    if (!file.exists()) {
        throw Exception("No build file found")
    }
    val lines = file.readLines()
    val output = getProp(lines, "output")
    val buildDir = getProp(lines, "buildDir")
    val main = getProp(lines, "main")
    val src = getProp(lines, "src")
    return BuildFile(output, buildDir, main, src)
}

fun getProp(lines: List<String>, prop: String): String {
    return lines.find { it.startsWith(prop) }?.split("=")?.get(1)?.trim() ?: throw Exception("No $prop found")
}