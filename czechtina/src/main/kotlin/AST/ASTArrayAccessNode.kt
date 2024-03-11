package AST

import compiler.types.InvalidType
import compiler.types.PointerType
import compiler.types.Type

class ASTArrayAccessNode(var array: ASTVariableNode, var index: ASTNode) : ASTVariableNode("", InvalidType()) {

    override fun getType(): Type {
        return if (array.getType() is PointerType) (array.getType() as PointerType).toDereference() else throw Exception("Invalid")
    }

    override fun copy(): ASTArrayAccessNode {
        return ASTArrayAccessNode(array.copy(), index.copy())
    }
    override fun retype(map: Map<Type, Type>) {
        array.retype(map)
        index.retype(map)
    }

    override fun toString(): String {
        return "ARRAY ACCESS, \narray=${array.toString().replace("\n","\n  ")}, \nindex=${index.toString().replace("\n","\n  ")}\n"
    }

    override fun toC(sideEffect:Boolean): String {
        return "${array.toC(false)}[${index.toC(false)}]"
    }
}