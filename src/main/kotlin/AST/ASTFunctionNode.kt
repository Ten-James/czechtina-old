package AST

class ASTFunctionNode : ASTNode {
    var type:ASTUnaryNode
    var name:String? = null
    var parameters:List<ASTNode> = listOf<ASTNode>()
    var body:ASTNode? = null

    constructor(type:ASTUnaryNode, name:String, parameters:List<ASTNode>, body:ASTNode) {
        this.type = type
        this.name = name
        this.parameters = parameters
        this.body = body
    }

    override fun toString(): String {
        return "\nFunction '$name' : '$type', \nparameters=${parameters.joinToString("").replace("\n","\n\t")}, \nbody=${body.toString().replace("\n","\n\t")}"
    }

    override fun toC(): String = "${type.toC()} ${name}(${parameters.joinToString(", ") { it.toC() }}) {\n${body?.toC()}\n}"
}