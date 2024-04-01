import java.io.File
import compiler.Compiler

fun main(args:Array<String>) {
    Printer.whoami = "czutils"

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
            Should be translated to:
            ```c
            int main() {
            
            }
            ```
            
            """.trimIndent()
        )
        return
    }

    if (args.any { it == "compile-test" }) {
        val name = getValueOfArg(args,"-n")
        val file = File("$name")
        if (!file.exists()) {
            Printer.fatal("Test with name $name does not exist")
            return
        }
        val text = file.readText()
        val splitText = text.split("```")
        val cCode = Compiler.compileText(splitText[1]).lines().filter { !it.startsWith("#include") }.joinToString("\n")
        Printer.info(cCode)
        file.createNewFile()
        file.writeText(listOf(splitText[0], "```",splitText[1] ,"```", splitText[2], "```c", cCode, "\n```", splitText[4]).joinToString(""))
    }

}

fun getValueOfArg(args: Array<String>,arg: String): String? {
    val index = args.indexOf(arg)
    if (index != -1 && index != args.size - 1) {
        return args[index + 1]
    }
    return null
}
