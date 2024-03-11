import java.io.File

class FileInfo(val path: String, var packageName: String = "", var lastModifiedTime: Long = 0) {
    val dependencies = mutableListOf<String>()

    override fun toString(): String {
        return """
            [$path]
            dependencies=${dependencies.joinToString(",")}
            package=$packageName
            modified=$lastModifiedTime
            
            
            """.trimIndent()
    }

    constructor(file: File) : this(file.path){
        val lines = file.readLines()
        val packageline = lines.find { it.startsWith("package") }
        if (packageline != null) {
            packageName = packageline.split(" ")[1]
        }
        val useLines = lines.filter { it.startsWith("use") }
        for (line in useLines) {
            dependencies.add(line.split(" ")[1])
        }
        lastModifiedTime = file.lastModified()

    }


    fun writeToFile(File: File) {
        File.appendText(
            """
            [$path]
            dependencies=${dependencies.joinToString(",")}
            package=$packageName
            modified=$lastModifiedTime
            
            
            """.trimIndent()
        )
    }
}

fun generateFileInfo(lines: List<String>): HashMap<String,FileInfo> {
    val fileInfo = HashMap<String,FileInfo>()
    var currentFileInfo: FileInfo? = null
    for (line in lines) {
        if (line.startsWith("[")) {
            if (currentFileInfo != null) {
                fileInfo[currentFileInfo.path] = currentFileInfo
            }
            currentFileInfo = FileInfo(line.substring(1, line.length - 1))
        } else {
            val split = line.split("=").map { it.trim() }
            if (split[0] == "dependencies") {
                if (split[1].contains(",")) {
                    currentFileInfo?.dependencies?.addAll(split[1].split(","))
                } else {
                    currentFileInfo?.dependencies?.add(split[1])
                }
            } else if (split[0] == "package") {
                currentFileInfo?.packageName = split[1]
            } else if (split[0] == "modified") {
                currentFileInfo?.lastModifiedTime = split[1].toLong()
            }
        }
    }
    if (currentFileInfo != null) {
        fileInfo[currentFileInfo.path] = currentFileInfo
    }
    return fileInfo
}
