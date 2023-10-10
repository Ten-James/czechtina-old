package AST

class ASTProgramLines : ASTNode {
    var programLines: List<ASTNode> = listOf<ASTNode>()

    constructor(programLines: List<ASTNode>) : super("programLines") {
        this.programLines = programLines
    }

    override fun toString(): String {
        return "\nLines ('$type')\n" + programLines.joinToString("").replace("\n", "\n\t")
    }
}