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
        return "\nImports:\n" + imports.joinToString("\n") +"Functions:\n" + functions.joinToString("\n") + "\n-----------------\nMain:\n" + main.toString()

    }

    override fun toC(): String = imports.joinToString("\n") { it.toC() } + "\n"+functions.joinToString("\n") { it.toC() } + "\n" + main?.toC()
}