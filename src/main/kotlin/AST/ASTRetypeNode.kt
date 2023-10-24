package AST

import compiler.DefinedType

class ASTRetypeNode: ASTTypedNode {
    val expression: ASTTypedNode;
    val type: ASTTypedNode;

    constructor(expression: ASTTypedNode, type: ASTTypedNode): super(DefinedType("")) {
        this.expression = expression;
        this.type = type;
    }

    override fun getType(): DefinedType {
        if (expression.getType().typeString.contains("-") && !type.getType().typeString.contains("-"))
            throw Exception("Cannot retype pointer to non-pointer type")
        if (type.getType().typeString.contains("arrau"))
            throw Exception("Cannot retype to array type")

        var type = type.getType();
        if (expression.getType().isHeap)
            type = type.toHeap()

        return type
    }

    override fun copy(): ASTRetypeNode {
        return ASTRetypeNode(expression.copy(), type.copy())
    }

    override fun toString(): String {
        return "Retype: ${expression} to ${type}"
    }

    override fun toC(sideEffect:Boolean): String {
        return "(${type.toC()})(${expression.toC()})"
    }
}