package AST

import compiler.DefinedType

class ASTRetypeNode(val expression: ASTNode, val type: ASTNode) : ASTNode(DefinedType("")) {

    override fun getType(): DefinedType {
        if (expression.getType().isAddress() && !this.type.getType().isAddress())
            throw Exception("Cannot retype pointer to non-pointer type")
        if (this.type.getType().typeString.contains("array"))
            throw Exception("Cannot retype to array type")

        var type = DefinedType(expression.getType())
        type.typeString = this.type.getType().typeString
        if (expression.getType().isDynamic())
            type = type.toDynamic()

        return type
    }

    override fun retype(map: Map<String, DefinedType>) {
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