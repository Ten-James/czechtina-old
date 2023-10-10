package AST

open class ASTNode {
    var type:String

    constructor(type:String) {
        this.type = type
    }

    override fun toString(): String {
        return "\nNode ('$type')"
    }
}