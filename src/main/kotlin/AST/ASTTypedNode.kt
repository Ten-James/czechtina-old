package AST


abstract class ASTTypedNode(var expType: String) : ASTNode() {

    open fun getType(): String = expType

}
