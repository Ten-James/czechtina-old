package AST

import compiler.DefinedType

class ASTListNode(val nodes: List<ASTNode>) : ASTNode(DefinedType("")) {

    override fun getType(): DefinedType {
        val types = nodes.map { it.getType() }
        val type = types[0]
        for (t in types) {
            if (t.typeString != type.typeString)
                throw Exception("Cant get type of array of variant types, $type and $t")
        }
        return type.toArray(types.size.toString());
    }

    override fun toString(): String {
        return nodes.joinToString("\n")
    }
    override fun copy(): ASTListNode {
        return ASTListNode(
            nodes.map { it.copy() }
        )
    }

    override fun retype(map: Map<String, DefinedType>) {
        nodes.forEach { it.retype(map) }
    }


    override fun toC(sideEffect:Boolean): String = nodes.joinToString(",") { it.toC() }

}