package AST

class ASTProgramNode : ASTNode {
    var imports: List<ASTUnaryNode> = listOf<ASTUnaryNode>()
    var functions: List<ASTFunctionNode> = listOf<ASTFunctionNode>()
    var typeDefinition = listOf<ASTBinaryNode>()
    var main: ASTFunctionNode? = null


    constructor(functions: List<ASTFunctionNode>, imports: List<ASTUnaryNode>, main: ASTFunctionNode?) {
        this.imports = imports
        this.functions = functions
        this.main = main
    }

    public fun appendFunction(function: ASTFunctionNode): ASTProgramNode {
        functions += function
        return this
    }

    public fun appendTypeDefinition(typeDefinition: ASTBinaryNode): ASTProgramNode {
        this.typeDefinition += typeDefinition
        return this
    }

    public fun appendImport(import: ASTUnaryNode): ASTProgramNode {
        imports += import
        return this
    }

    override fun retype(map: Map<String, String>) {
        functions.forEach { it.retype(map) }
        main?.retype(map)
    }

    override fun copy(): ASTProgramNode {
        return ASTProgramNode(
            functions.map { it.copy() },
            imports.map { it.copy() },
            main?.copy()
        )
    }

    override fun toString(): String {
        return "Imports:\n" + imports.joinToString("\n") + "\nType definitions:\n" + typeDefinition.joinToString("\n") + "\nFunctions:\n" + functions.joinToString(
            "\n"
        ) + "\n-----------------\nMain:\n" + main.toString()

    }

    fun doubleNewLine(condition:Boolean) = if (condition) "\n\n" else ""
    override fun toC(): String =
        imports.joinToString("\n") { it.toC() } +
                doubleNewLine(imports.isNotEmpty()) + functions.joinToString("\n") { it.toCDeclaration() } +
                doubleNewLine(functions.isNotEmpty()) + typeDefinition.joinToString("\n") { it.toC() } +
                doubleNewLine(typeDefinition.isNotEmpty()) + functions.joinToString("\n\n") { it.toC() } +
                doubleNewLine(functions.isNotEmpty()) + main?.toC()
}