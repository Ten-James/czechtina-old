package AST

import compiler.DefinedType

class ASTArrayAccessNode: ASTVariableNode {
    var array:ASTVariableNode
    var index:ASTNode

    constructor(array:ASTVariableNode, index:ASTNode): super("", DefinedType("")) {
        this.array = array
        this.index = index
    }

    override fun getType(): DefinedType {
        return DefinedType( array.getType().typeString.split("-")[1], array.getType().isHeap, array.getType().isConst)
    }

    override fun copy(): ASTArrayAccessNode {
        return ASTArrayAccessNode(array.copy(), index.copy())
    }
    override fun retype(map: Map<String, DefinedType>) {
        array.retype(map)
        index.retype(map)
    }

    override fun toString(): String {
        return "ARRAY ACCESS, \narray=${array.toString().replace("\n","\n\t")}, \nindex=${index.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(sideEffect:Boolean): String {
        return "${array.toC()}[${index.toC()}]"
    }
}