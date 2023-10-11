package AST

class ASTListNode : ASTNode {
    val nodes: List<ASTNode>

    constructor(nodes: List<ASTNode>) {
        this.nodes = nodes
    }

    override fun toString(): String {
        return nodes.joinToString("\n")
    }

    override fun toC(): String = nodes.joinToString(",") { it.toC() }

}