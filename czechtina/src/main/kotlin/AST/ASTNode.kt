package AST

import compiler.types.Type

abstract class ASTNode(var expType: Type) {

    abstract fun toC(sideEffect:Boolean = true): String

    abstract fun copy(): ASTNode

    open fun getType(): Type = expType.copy()

    open fun retype(map: Map<Type, Type>) {
        expType = expType.reType(map)
    }

}
