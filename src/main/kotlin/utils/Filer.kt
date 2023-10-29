package utils

import java.io.File

object Filer {
    fun readFromFile(path: String): String {
        return File(path).readText()
    }
}