package AST

class ASTProgramNode : ASTNode {
    var imports: List<ASTUnaryNode> = listOf<ASTUnaryNode>()
    var functions: List<ASTFunctionNode> = listOf<ASTFunctionNode>()
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

    public fun appendImport(import: ASTUnaryNode): ASTProgramNode {
        imports += import
        return this
    }

    override fun toString(): String {
        return "Imports:\n" + imports.joinToString("\n") +"\nFunctions:\n" + functions.joinToString("\n") + "\n-----------------\nMain:\n" + main.toString()

    }

    override fun toC(): String = imports.joinToString("\n\n") { it.toC() } + "\n\n"+functions.joinToString("\n\n") { it.toC() } + "\n\n" + main?.toC()
}