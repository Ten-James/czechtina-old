package AST

open abstract class ASTNode {

    abstract fun toC(): String
}