package AST

class ASTArrayAccessNode: ASTTypedNode {
    var array:ASTVariableNode
    var index:ASTNode

    constructor(array:ASTVariableNode, index:ASTNode): super("") {
        this.array = array
        this.index = index
    }

    override fun getType(): String {
        return array.getType().split("-")[1]
    }

    override fun toString(): String {
        return "ARRAY ACCESS, \narray=${array.toString().replace("\n","\n\t")}, \nindex=${index.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(): String {
        return "${array.toC()}[${index.toC()}]"
    }
}