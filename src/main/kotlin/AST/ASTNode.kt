package AST

import compiler.DefinedType

 abstract class ASTNode(var expType: DefinedType) {

    abstract fun toC(sideEffect:Boolean = true): String

    abstract fun copy(): ASTNode

    open fun getType(): DefinedType = expType

    open fun retype(map: Map<String, DefinedType>) {
        for (m in map)
            if (expType.typeString == m.key)
                expType = m.value
    }

}
