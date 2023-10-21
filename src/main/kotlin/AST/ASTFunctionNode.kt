package AST

import compiler.Compiler

class ASTFunctionNode : ASTNode {
    var type:ASTUnaryNode
    var name:String? = null
    var parameters:List<ASTNode> = listOf<ASTNode>()
    var body:ASTUnaryNode? = null

    constructor(type:ASTUnaryNode, name:String, parameters:List<ASTNode>, body:ASTUnaryNode) {
        this.type = type
        this.name = name
        this.parameters = parameters
        this.body = body
        unscopeBody()
    }

    fun unscopeBody(){
        if (body is ASTUnaryNode)
        {
            (body as ASTUnaryNode).type = ASTUnaryTypes.CURLY_UNSCOPE
        }
    }


    override fun toString(): String {
        return "Function '$name' : '$type', \nparameters=${parameters.joinToString("").replace("\n","\n\t")}, \nbody=${body.toString().replace("\n","\n\t")}"
    }

    override fun toC(): String  {
        return "${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}) ${body?.toC()}"
    }

    fun toCDeclaration(): String {
        return "${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}); ${Compiler.scopePop(false)}"
    }
}