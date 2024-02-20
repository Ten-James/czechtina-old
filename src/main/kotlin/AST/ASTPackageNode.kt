package AST

class ASTPackageNode(var packageName: String) : ASTProgramNode(emptyList(), emptyList(), null) {


    override fun toC(sideEffect:Boolean): String =
        "#ifndef $packageName \n#define $packageName\n" +
        imports.joinToString("\n") { it.toC() } +
                doubleNewLine(imports.isNotEmpty()) + structures.joinToString("\n") { it.toC() } +
                doubleNewLine(structures.isNotEmpty()) + functions.joinToString("\n") { it.toCDeclaration() } +
                doubleNewLine(functions.isNotEmpty()) + typeDefinition.joinToString("\n") { it.toC() } +
                doubleNewLine(typeDefinition.isNotEmpty()) + functions.joinToString("\n\n") { it.toC() } +
                "\n#endif"
}