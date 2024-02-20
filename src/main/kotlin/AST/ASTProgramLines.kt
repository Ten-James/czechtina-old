package AST

import compiler.DefinedType

class ASTProgramLines(var programLines: List<ASTNode>) : ASTNode(DefinedType("")) {


    override fun toString(): String {
        return "Lines:\n${programLines.joinToString("").replace("\n", "\n\t")}\n"
    }

    override fun copy(): ASTProgramLines {
        return ASTProgramLines(
            programLines.map { it.copy() }
        )
    }

    override fun retype(map: Map<String, DefinedType>) {
        programLines.forEach { it.retype(map) }
    }

    override fun toC(sideEffect:Boolean): String = programLines.joinToString("\n") { it.toC() }
}