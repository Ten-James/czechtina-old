package AST

import compiler.Compiler
import compiler.DefinedStructure
import compiler.types.InvalidType
import compiler.types.StructureType
import compiler.types.Type
import java.util.*

class ASTStructureNode: ASTNode {
    val name: String
    val properties : MutableList<ASTVarDefinitionNode>
    val functions = mutableListOf<ASTFunctionNode>()

    constructor(name: String, props: List<ASTVarDefinitionNode>) : super(StructureType(name)) {
        if (name.uppercase(Locale.getDefault()) != name)
            throw Exception("Structure name must be in Uppercase")
        this.name = name.uppercase(Locale.getDefault())
        this.properties = props.toMutableList()
    }

    fun defineItSelf(): ASTStructureNode {
        val structureType = StructureType(name)
        Compiler.addToTypes(name, structureType)
        Compiler.definedStructures += mapOf(structureType.funcName() to
            DefinedStructure(name, structureType, mutableMapOf())
        )
        return this
    }

    fun addProperty(prop: ASTVarDefinitionNode):ASTStructureNode {
        val type = getType() as StructureType
        Compiler.definedStructures[type.funcName()]!!.addPropType(prop.variable.data, prop.getType())
        properties.add(prop)
        return this
    }

    fun addFunction(func: ASTFunctionNode):ASTStructureNode {
        val dest = func.name == "destruct"
        val typ = getType() as StructureType
        func.name = "${typ.funcName()}_${func.name}"
        val thisParam =  ASTVarDefinitionNode(ASTVariableNode("this", InvalidType()), ASTUnaryNode(ASTUnaryTypes.TYPE, "", this.getType()))
        func.parameters = listOf(thisParam) + func.parameters

        Compiler.definedStructures[typ.funcName()]!!.addFunction(func.name!!)
        functions.add(func)
        return this
    }

    override fun toC(sideEffect:Boolean): String {
        return "typedef struct ${Compiler.scopePush()}{\n\t${properties.joinToString(";\n\t") { it.toC() }};\n ${Compiler.scopePop(false)}} $name;"
    }

    override fun retype(map: Map<Type, Type>) {
        properties.forEach{it.retype(map)}
    }

    override fun copy(): ASTStructureNode {
        return ASTStructureNode(name, properties.map { it.copy() })
    }
}