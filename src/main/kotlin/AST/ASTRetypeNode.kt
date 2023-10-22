package AST

class ASTRetypeNode: ASTTypedNode {
    val expression: ASTTypedNode;
    val type: ASTTypedNode;

    constructor(expression: ASTTypedNode, type: ASTTypedNode): super("") {
        this.expression = expression;
        this.type = type;
    }

    override fun getType(): String {
        if (expression.getType().contains("-") && !type.getType().contains("-"))
            throw Exception("Cannot retype pointer to non-pointer type")
        if (type.getType().contains("arrau"))
            throw Exception("Cannot retype to array type")

        var type = type.getType();

        if (expression.getType().contains("dynamic"))
            type =type.replace("pointer", "dynamic")
        return type
    }

    override fun copy(): ASTRetypeNode {
        return ASTRetypeNode(expression.copy(), type.copy())
    }

    override fun toString(): String {
        return "Retype: ${expression.toString()} to ${type.toString()}"
    }

    override fun toC(): String {
        return "(${type.toC()})(${expression.toC()})"
    }
}