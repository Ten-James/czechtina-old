package utils

import FileInfo
import generateFileInfo
import java.io.File

object DependencyFile {
    val filesInfo = hashMapOf<String, FileInfo>()

    fun readDependencies() {
        val file = File(".czcache")
        if (file.exists()) {
            val lines = file.readLines()
            filesInfo.putAll(generateFileInfo(lines))
        }
    }

    fun writeDependencies() {
        val file = File(".czcache")
        file.delete()
        file.createNewFile()
        for (fileInfo in filesInfo) {
            fileInfo.value.writeToFile(file)
        }
    }

    fun walkFiles(force:Boolean): List<String> {
        val toCompile = mutableSetOf<String>()
        val build = readBuildFile();
        val srcdir = File(build.src)
        srcdir.walk().forEach {
            if (it.isFile) {
                if (it.lastModified() > (filesInfo[it.path]?.lastModifiedTime ?: 0) || force) {
                    filesInfo[it.path] = FileInfo(it)
                    toCompile.add(it.path)
                }
            }
        }

        srcdir.walk().forEach {
            if (it.isFile) {
                if (toCompile.contains(it.path)) {
                    val packageName = filesInfo[it.path]?.packageName
                    if (!packageName.isNullOrEmpty()) {
                        filesInfo.values.filter { it.dependencies.contains(packageName) }.forEach {
                            toCompile.add(it.path)
                        }
                    }
                }
            }
        }

        writeDependencies()

        return toCompile.toList()
    }
}

