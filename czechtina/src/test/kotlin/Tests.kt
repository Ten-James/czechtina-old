import compiler.Compiler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import utils.ArgsProvider
import java.io.File

class Tests {

    @TestFactory
    fun basicsTest(): List<DynamicTest> = getTests("basics")

    @TestFactory
    fun codeBlocksTest(): List<DynamicTest> = getTests("codeBlocks")

    @TestFactory
    fun dynamicTest(): List<DynamicTest> = getTests("dynamic")

    @TestFactory
    fun featuresTest(): List<DynamicTest> = getTests("features")
    @TestFactory
    fun structsTest(): List<DynamicTest> = getTests("structs")
    @TestFactory
    fun typesTest(): List<DynamicTest> = getTests("types")
    @TestFactory
    fun virtualTest(): List<DynamicTest> = getTests("virtual")
    private fun getTests(subPath: String): List<DynamicTest>{
        val file = File("src/test/resources/$subPath")
        if (!file.exists()) {
            throw Exception("No folder found")
        }
        return file.walk().filter { it.isFile() }.map { it.path }.toList().map { dynamicTest(it) }
    }

    private fun dynamicTest(it: String): DynamicTest =
        DynamicTest.dynamicTest(it.substringAfterLast("/")) {
            Printer.setDebug()
            val text = File("$it").readText()
            val splitText = text.split("```")
            val cCode =
                Compiler.compileText(splitText[1]).lines().filter { !it.startsWith("#include") }.joinToString("\n")
            Printer.lowInfo(cCode)
            val exceptedCode = if (splitText[3].startsWith('c')) splitText[3].substring(1) else splitText[3]

            Assertions.assertEquals(
                exceptedCode.replace("\\s+".toRegex(), " ").trim(),
                cCode.replace("\\s+".toRegex(), " ").trim()
            )

        }
}