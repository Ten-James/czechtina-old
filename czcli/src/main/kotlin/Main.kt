import utils.DependencyFile
import utils.readBuildFile
import java.io.File

fun main(args: Array<String>) {

    var shouldReturn = false

    Printer.whoami = "czcli"

    if (args.isEmpty()) {
        Printer.printHelp()
        return
    }

    DependencyFile.readDependencies()


    if (args.any { it == "clean" }) {
        try {
            val buildFile = readBuildFile()
            val buildDir = File(buildFile.buildDir)
            if (buildDir.exists()) {
                buildDir.deleteRecursively()
            }
            Printer.info("Build directory cleaned")
        } catch (e: Exception) {
            Printer.fatal(e.message ?: "Error")
        }

    }

    if (args.any { it == "init" }) {
        val defaultName = getValueOfArg(args,"-n")
        val defaultPath = getValueOfArg(args,"-d")
        Init(defaultName, defaultPath)
        return
    }

    if (args.any { it == "build"}) {
        BuildProject(args.any { it == "--debug" }, true)
        shouldReturn = true
    }

    if (args.any { it == "info"}) {
        try {
            val buildFile = readBuildFile()
            println(Printer.greenWrap("Project: ${buildFile.Output}"))
            println("Build directory: ${buildFile.buildDir}")
            println("Main file: ${buildFile.main}")
            println("Source directory: ${buildFile.src}")
        } catch (e: Exception) {
            Printer.fatal(e.message ?: "Error")
        }
        return
    }

    if (args.any { it == "run"}) {
        Run(args.any { it == "--debug" })
        shouldReturn = true
    }

    if (args.any { it == "make-test" }) {
        val name = getValueOfArg(args,"-n")
        val file = File("$name.md")
        if (file.exists()) {
            Printer.fatal("Test with name $name already exists")
            return
        }
        file.createNewFile()
        file.writeText(
            """
            # $name test
            
            ```
            main {
                // Your code here
            }
            ```
            
            ```c
            int main() {
            
            }
            ```
            
            """.trimIndent()
        )
        return
    }

    if (shouldReturn) {
        DependencyFile.writeDependencies()
        return
    }

    Printer.printCLIHelp()
}


fun getValueOfArg(args: Array<String>,arg: String): String? {
    val index = args.indexOf(arg)
    if (index != -1 && index != args.size - 1) {
        return args[index + 1]
    }
    return null
}
