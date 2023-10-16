package AST

class ASTListNode : ASTNode {
    val nodes: List<ASTTypedNode>

    constructor(nodes: List<ASTTypedNode>) {
        this.nodes = nodes
    }

    fun getType():String {
        val types = nodes.map { it.getType() }
        val type = types[0]
        for (t in types) {
            if (t != type)
                throw Exception("Cant get type of array of variant types")
        }
        return type;
    }

    override fun toString(): String {
        return nodes.joinToString("\n")
    }



    override fun toC(): String = nodes.joinToString(",") { it.toC() }

}