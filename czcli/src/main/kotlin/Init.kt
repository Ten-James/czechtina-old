import java.io.File

fun Init(defaultName: String?, defaultPath:String?) {
    var name = defaultName
    var path = defaultPath
    while (name == null) {
        println(Printer.greenWrap("Enter the name of the project:"))
         name = readlnOrNull()
    }

    path = (if (path.isNullOrEmpty()) name else if (!path.endsWith("/")) "$path/$name" else "$path$name")

    createDir(path ?: "")
    createDir("$path/src")

    // create file main.cz in src
    createFile("$path/src/main.cz", """
        /// This is the main file of the project
        
        main {
            println "Hello, World!"
        }
    """.trimIndent())
    // create .buildczech file

    createFile("$path/.buildczech", """
        // This is the build file of the project
        output = "$name"
        buildDir = "build"
        main = "src/main.cz"
        src = "src"
        
    """.trimIndent())

    createFile("$path/.gitignore", """
        ### Czechtina ###
        build/
        .czcache
    """.trimIndent())

    createFile("$path/README.md", """
        # $name
        This is the main file of the project
    """.trimIndent())

    println(Printer.greenWrap("Project $name created in $path"))
    println()
    println(" To run the project, run:")
    println(Printer.greenWrap("   czcli run"))
    println()
    println(" Go to $name and start czoding:")
    println(Printer.greenWrap("   cd $name"))

}

fun createDir(path: String) {
    val dir = File(path)
    if (!dir.exists()) {
        dir.mkdirs()
    }
}

fun createFile(path: String, context: String) {
    val file = File(path)
    file.createNewFile()
    file.writeText(context)
}