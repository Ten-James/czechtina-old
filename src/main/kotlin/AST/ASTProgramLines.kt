package AST

class ASTProgramLines : ASTNode {
    var programLines: List<ASTNode> = listOf<ASTNode>()

    constructor(programLines: List<ASTNode>)  {
        this.programLines = programLines
    }


    override fun toString(): String {
        return "Lines:\n${programLines.joinToString("").replace("\n", "\n\t")}\n"
    }

    override fun copy(): ASTProgramLines {
        return ASTProgramLines(
            programLines.map { it.copy() }
        )
    }

    override fun retype(map: Map<String, String>) {
        programLines.forEach { it.retype(map) }
    }

    override fun toC(): String = programLines.joinToString("\n") { it.toC() }
}