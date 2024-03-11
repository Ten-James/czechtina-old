package AST

import compiler.types.InvalidType
import compiler.types.Type

class ASTProgramLines(var programLines: List<ASTNode>) : ASTNode(InvalidType()) {


    override fun toString(): String {
        return "Lines:\n  ${programLines.joinToString("\n\n").replace("\n", "\n  ")}"
    }

    override fun copy(): ASTProgramLines {
        return ASTProgramLines(
            programLines.map { it.copy() }
        )
    }

    override fun retype(map: Map<Type, Type>) {
        programLines.forEach { it.retype(map) }
    }

    override fun toC(sideEffect:Boolean): String = programLines.joinToString("\n") { it.toC() }
}