package AST

open class ASTBinaryNode : ASTNode {
    var left:ASTNode? = null
    var right:ASTNode? = null

    constructor(type:String,left:ASTNode, right:ASTNode) : super(type) {
        this.left = left
        this.right = right
    }

    override fun toString(): String {
        return "\n'$type', \nleft=${left.toString().replace("\n","\n\t")}, \nright=${right.toString().replace("\n","\n\t")}"
    }
}