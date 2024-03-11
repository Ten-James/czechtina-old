import java.io.File

object Filer {
    fun readFromFile(path: String): String {
        return File(path).readText()
    }

    fun createAllDirsInPath(path: String) {
        Printer.info("Creating directories in path $path")
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }
}