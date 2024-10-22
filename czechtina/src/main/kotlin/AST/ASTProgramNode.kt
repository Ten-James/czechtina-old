package AST

import compiler.types.InvalidType
import compiler.types.Type

open class ASTProgramNode(
    var functions: List<ASTFunctionNode>,
    var imports: List<ASTUnaryNode>,
    var main: ASTFunctionNode?
) : ASTNode(InvalidType()) {
    var structures: List<ASTStructureNode> = listOf()
    var typeDefinition = listOf<ASTBinaryNode>()


    init {
        structures = emptyList()
    }

    fun appendFunction(function: ASTFunctionNode): ASTProgramNode {
        functions += function
        return this
    }

    fun appendTypeDefinition(typeDefinition: ASTBinaryNode): ASTProgramNode {
        this.typeDefinition += typeDefinition
        return this
    }

    fun appendImport(import: ASTUnaryNode): ASTProgramNode {
        imports += import
        return this
    }

    fun appendStructure(struct: ASTStructureNode): ASTProgramNode {
        structures += struct
        this.functions += struct.functions
        return this
    }

    override fun retype(map: Map<Type, Type>) {
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
    override fun toC(sideEffect:Boolean): String =
        imports.joinToString("\n") { it.toC() } +
                doubleNewLine(imports.isNotEmpty()) + structures.joinToString("\n") { it.toC() } +
                doubleNewLine(structures.isNotEmpty()) + functions.joinToString("\n") { it.toCDeclaration() } +
                doubleNewLine(functions.isNotEmpty()) + typeDefinition.joinToString("\n") { it.toC() } +
                doubleNewLine(typeDefinition.isNotEmpty()) + functions.joinToString("\n\n") { it.toC() } +
                doubleNewLine(functions.isNotEmpty()) + main?.toC()

    fun toCDeclaration():String =
        imports.joinToString("\n") { it.toC() } +
                doubleNewLine(imports.isNotEmpty()) + structures.joinToString("\n") { it.toC() } +
                doubleNewLine(structures.isNotEmpty()) + functions.joinToString("\n") { it.toCDeclaration() } +
                doubleNewLine(functions.isNotEmpty()) + typeDefinition.joinToString("\n") { it.toC() }
}