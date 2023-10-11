package AST


enum class ASTBinaryTypes {
    VAR_DEFINITION,
    FUNCTION_CALL,
}

open class ASTBinaryNode : ASTNode {
    var type:ASTBinaryTypes? = null
    var left:ASTNode? = null
    var right:ASTNode? = null

    constructor(type:ASTBinaryTypes?,left:ASTNode, right:ASTNode) {
        this.type = type
        this.left = left
        this.right = right
    }

    override fun toString(): String {
        return "\n'$type', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}"
    }

    override fun toC(): String = when (type) {
        ASTBinaryTypes.VAR_DEFINITION -> "${left?.toC()} ${right?.toC()}"
        ASTBinaryTypes.FUNCTION_CALL -> "${left?.toC()}(${right?.toC()})"
        else -> ""
    }
}