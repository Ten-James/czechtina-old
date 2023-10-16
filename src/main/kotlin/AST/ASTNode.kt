package AST

import czechtina.GrammarToken

open abstract class ASTNode {

    abstract fun toC(): String
}
