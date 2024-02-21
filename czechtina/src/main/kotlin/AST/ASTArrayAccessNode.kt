package AST

import compiler.DefinedType

class ASTArrayAccessNode(var array: ASTVariableNode, var index: ASTNode) : ASTVariableNode("", DefinedType("")) {

    override fun getType(): DefinedType {
        return array.getType().toDereference()
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
        return "${array.toC(false)}[${index.toC(false)}]"
    }
}