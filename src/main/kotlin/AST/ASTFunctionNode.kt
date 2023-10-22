package AST

import compiler.Compiler
import compiler.DefinedType

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
        if (body is ASTUnaryNode) {
            (body as ASTUnaryNode).type = ASTUnaryTypes.CURLY_UNSCOPE
        }
    }

    override fun retype(map: Map<String, DefinedType>) {
        type.retype(map)
        parameters.forEach { it.retype(map) }
        body?.retype(map)
    }


    override fun toString(): String {
        return "Function '$name' : '$type', \nparameters=${parameters.joinToString("").replace("\n","\n\t")}, \nbody=${body.toString().replace("\n","\n\t")}"
    }

    override fun toC(): String  {
        val paramsTypes = mutableListOf<DefinedType>()
        for (param in parameters)
            if (param is ASTTypedNode)
                paramsTypes.add(param.getType())
        if (paramsTypes.any{it.isTemplate()})
            return "//${name}_CZECHTINA ANCHOR\n"
        return "//${name}_CZECHTINA ANCHOR\n${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}) ${body?.toC()}"
    }

    override fun copy(): ASTFunctionNode {
        return ASTFunctionNode(type.copy(), name!!, parameters.map { it.copy() }, body!!.copy())
    }

    fun toCNoSideEffect(): String = "${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}) ${body?.toC()}"


    fun toCDeclarationNoSideEffect(): String = "${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}); ${Compiler.scopePop(false)}"

    fun toCDeclaration(): String {
        val paramsTypes = mutableListOf<DefinedType>()
        for (param in parameters)
            if (param is ASTTypedNode)
                paramsTypes.add(param.getType())

        if (Compiler.definedFunctions.containsKey(name!!)) {
            val newName = "${name}_v${Compiler.definedFunctions[name!!]!!.variants.size}"
            Compiler.definedFunctions[name!!]!!.variants.add(compiler.DefinedFunctionVariant(newName, paramsTypes))
            name = newName;
        }
        else
            Compiler.definedFunctions += mapOf(name!! to compiler.DefinedFunction(name!!, compiler.DefinedType(type.toC()), listOf(compiler.DefinedFunctionVariant(name!!, paramsTypes)), virtual = false))
        if (paramsTypes.any{it.isTemplate()})
            return "//${name}_Declaration_CZECHTINA ANCHOR\n"

        return "//${name}_Declaration_CZECHTINA ANCHOR\n"+toCDeclarationNoSideEffect()
    }
}