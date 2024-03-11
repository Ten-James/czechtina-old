package AST

import compiler.Compiler
import compiler.types.InvalidType
import compiler.types.Type

class ASTFunctionNode(var type: ASTUnaryNode, name: String, var parameters: List<ASTNode>, body: ASTUnaryNode) :
    ASTNode(InvalidType()) {
    var name:String? = name
    var body:ASTUnaryNode? = body

    init {
        unscopeBody()
    }

    private fun unscopeBody(){
        if (body is ASTUnaryNode) {
            (body as ASTUnaryNode).type = ASTUnaryTypes.CURLY_UNSCOPE
        }
    }

    override fun retype(map: Map<Type, Type>) {
        type.retype(map)
        parameters.forEach { it.retype(map) }
        body?.retype(map)
    }


    override fun toString(): String {
        return "Function '$name': $type, \nparameters=${if (parameters.isEmpty()) { "none" } else {parameters.joinToString("").replace("\n","\n  ")}}, \nbody=${body.toString().replace("\n","\n  ")}"
    }

    override fun toC(sideEffect:Boolean): String  {
        val paramsTypes = mutableListOf<Type>()
        for (param in parameters)
            paramsTypes.add(param.getType())
        if (paramsTypes.any{ false}) //TODO
            return "//${name}_CZECHTINA ANCHOR\n"
        return "//${name}_CZECHTINA ANCHOR\n${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}) ${body?.toC()}"
    }

    override fun copy(): ASTFunctionNode {
        return ASTFunctionNode(type.copy(), name!!, parameters.map { it.copy() }, body!!.copy())
    }

    fun precalculateType() {
        if (type.expType is InvalidType)
            return

        Compiler.scopePush()
        for (param in parameters)
            Compiler.setNewVariableType((param as ASTVarDefinitionNode).variable.data, param.type.getType())
        if (type.getType() is InvalidType)
            type.expType = body!!.getType()
        Compiler.scopePop(false)
    }

    fun toCNoSideEffect(): String = "${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}) ${body?.toC()}"


    fun toCDeclarationNoSideEffect(): String = "${type.toC()}${Compiler.scopePush()} ${name}(${parameters.joinToString(", ") { it.toC() }}); ${Compiler.scopePop(false)}"

    fun toCDeclaration(): String {
        val paramsTypes = mutableListOf<Type>()
        for (param in parameters)
            paramsTypes.add(param.getType())

        if (Compiler.definedFunctions.containsKey(name!!)) {
            val newName = "${name}_v${Compiler.definedFunctions[name!!]!!.variants.size}"
            Compiler.definedFunctions[name!!]!!.variants.add(compiler.DefinedFunctionVariant(newName, paramsTypes, returnType = type.getType()))
            name = newName;
        }
        else
            Compiler.definedFunctions += mapOf(name!! to compiler.DefinedFunction(name!!, type.getType(), listOf(compiler.DefinedFunctionVariant(name!!, paramsTypes, returnType = type.getType())), virtual = false))
        if (paramsTypes.any{false}) //TODO
            return "//${name}_Declaration_CZECHTINA ANCHOR\n"

        return "//${name}_Declaration_CZECHTINA ANCHOR\n"+toCDeclarationNoSideEffect()
    }
}