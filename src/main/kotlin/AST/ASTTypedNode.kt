package AST


abstract class ASTTypedNode(var expType: String) : ASTNode() {

    open fun getType(): String = expType

    abstract override fun copy(): ASTTypedNode

    override fun retype(map: Map<String, String>) {
        for (m in map)
            expType = expType.replace(m.key, m.value)
    }

}
