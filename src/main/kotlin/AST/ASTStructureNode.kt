package AST

import compiler.Compiler
import compiler.DefinedStructure
import compiler.DefinedType
import java.util.*

class ASTStructureNode: ASTNode {
    val name: String
    val properties : MutableList<ASTVarDefinitionNode>
    val functions = mutableListOf<ASTFunctionNode>()

    constructor(name: String, props: List<ASTVarDefinitionNode>) : super(DefinedType("pointer-$name", isStructured = true, isHeap = true)) {
        if (name.uppercase(Locale.getDefault()) != name)
            throw Exception("Structure name must be in Uppercase")
        this.name = name.uppercase(Locale.getDefault())
        this.properties = props.toMutableList()
    }

    fun defineItSelf(): ASTStructureNode {
        val structureType = DefinedType("pointer-$name", isStructured = true, isHeap = true)
        Compiler.addToDefinedTypes(name, structureType)
        Compiler.definedStructures += mapOf(name to
            DefinedStructure(name, structureType, mutableMapOf())
        )
        return this
    }

    fun addProperty(prop: ASTVarDefinitionNode):ASTStructureNode {
        Compiler.definedStructures[name]!!.addPropType(prop.variable.data, prop.getType())
        properties.add(prop)
        return this
    }

    fun addFunction(func: ASTFunctionNode):ASTStructureNode {
        val dest = func.name == "destruct"
        func.name = "${name}_${func.name}"
        val thisParam =  ASTVarDefinitionNode(ASTVariableNode("this", DefinedType("none")), ASTUnaryNode(ASTUnaryTypes.TYPE, "", DefinedType("${if (dest) "dynamic" else "pointer" }-$name", isStructured = true, isHeap = true)))
        func.parameters = listOf(thisParam) + func.parameters

        Compiler.definedStructures[name]!!.addFunction(func.name!!)
        functions.add(func)
        return this
    }

    override fun toC(sideEffect:Boolean): String {
        return "typedef struct ${Compiler.scopePush()}{\n\t${properties.joinToString(";\n\t") { it.toC() }};\n ${Compiler.scopePop(false)}} $name;"
    }

    override fun retype(map: Map<String, DefinedType>) {
        properties.forEach{it.retype(map)}
    }

    override fun copy(): ASTStructureNode {
        return ASTStructureNode(name, properties.map { it.copy() })
    }
}