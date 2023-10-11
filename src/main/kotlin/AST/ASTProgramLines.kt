package AST

class ASTProgramLines : ASTNode {
    var programLines: List<ASTNode> = listOf<ASTNode>()

    constructor(programLines: List<ASTNode>)  {
        this.programLines = programLines
    }


    override fun toString(): String {
        return "\nLines:\n" + programLines.joinToString("").replace("\n", "\n\t")
    }

    override fun toC(): String = programLines.joinToString(";\n") { it.toC() }+";"
}