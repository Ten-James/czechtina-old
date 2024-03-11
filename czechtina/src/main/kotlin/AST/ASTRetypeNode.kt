package AST

import compiler.types.DynamicPointerType
import compiler.types.Type
import compiler.types.InvalidType
import compiler.types.PointerType

class ASTRetypeNode(val expression: ASTNode, val type: ASTNode) : ASTNode(InvalidType()) {

    override fun getType(): Type {
        if (expression.getType() is PointerType && this.type.getType() !is PointerType)
            throw Exception("Cannot retype pointer to non-pointer type")
        //if (this.type.getType().typeString.contains("array"))
        //    throw Exception("Cannot retype to array type")

        var type = type.getType()
        if (expression.getType() is DynamicPointerType)
            type = DynamicPointerType((type as PointerType).toDereference())

        return type
    }

    override fun retype(map: Map<Type, Type>) {
        expression.retype(map)
        type.retype(map)
    }
    override fun copy(): ASTRetypeNode {
        return ASTRetypeNode(expression.copy(), type.copy())
    }

    override fun toString(): String {
        return "Retype: $expression to $type"
    }

    override fun toC(sideEffect:Boolean): String {
        return "(${type.toC()})(${expression.toC()})"
    }
}