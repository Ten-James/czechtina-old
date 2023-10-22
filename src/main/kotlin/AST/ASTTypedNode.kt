package AST

import compiler.DefinedType


abstract class ASTTypedNode(var expType: DefinedType) : ASTNode() {

    open fun getType(): DefinedType = expType

    abstract override fun copy(): ASTTypedNode

    override fun retype(map: Map<String, DefinedType>) {
        for (m in map)
            if (expType.typeString == m.key)
                expType = m.value
    }

}
