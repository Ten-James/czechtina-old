package AST

import java.util.*

class ASTPackageNode(var packageName: String) : ASTProgramNode(emptyList(),
    listOf(
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "stdio"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "stdlib"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "malloc"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "string"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "stdbool"),
        ASTUnaryNode(ASTUnaryTypes.IMPORT_C, "math"),
    )

        , null) {

    fun getPackageNodeName() = packageName.replace("::", "_").uppercase(Locale.getDefault())


    override fun toC(sideEffect:Boolean): String =
        "#ifndef ${getPackageNodeName()} \n#define ${getPackageNodeName()}\n" +
        imports.joinToString("\n") { it.toC() } +
                doubleNewLine(imports.isNotEmpty()) + structures.joinToString("\n") { it.toC() } +
                doubleNewLine(structures.isNotEmpty()) + functions.joinToString("\n") { it.toCDeclaration() } +
                doubleNewLine(functions.isNotEmpty()) + typeDefinition.joinToString("\n") { it.toC() } +
                doubleNewLine(typeDefinition.isNotEmpty()) + functions.joinToString("\n\n") { it.toC() } +
                "\n#endif"
}