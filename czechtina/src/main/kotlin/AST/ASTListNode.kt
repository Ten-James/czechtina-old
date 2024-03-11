package AST

import compiler.types.InvalidType
import compiler.types.StaticArrayType
import compiler.types.Type

class ASTListNode(val nodes: List<ASTNode>) : ASTNode(InvalidType()) {

    override fun getType(): Type {
        val types = nodes.map { it.getType() }
        val type = types[0]
        for (t in types) {
            if (t != type)
                throw Exception("Cant get type of array of variant types: $type and $t")
        }
        return StaticArrayType(type, types.size.toString())
    }

    override fun toString(): String {
        return nodes.joinToString(",")
    }
    override fun copy(): ASTListNode {
        return ASTListNode(
            nodes.map { it.copy() }
        )
    }

    override fun retype(map: Map<Type, Type>) {
        nodes.forEach { it.retype(map) }
    }


    override fun toC(sideEffect:Boolean): String = nodes.joinToString(",") { it.toC() }

}