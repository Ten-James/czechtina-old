package AST

import czechtina.GrammarToken

open abstract class ASTNode {

    abstract fun toC(): String

    abstract fun copy(): ASTNode

    abstract fun retype(map: Map<String, String>)
}
