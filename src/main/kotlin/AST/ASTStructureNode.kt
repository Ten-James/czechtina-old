package AST

import compiler.Compiler
import compiler.DefinedStructure
import compiler.DefinedType
import java.util.*

class ASTStructureNode: ASTNode {
    val name: String
    val properties : MutableList<ASTVarDefinitionNode>

    constructor(name: String, props: List<ASTVarDefinitionNode>) {
        if (name.uppercase(Locale.getDefault()) != name)
            throw Exception("Structure name must be in Uppercase")
        this.name = name.uppercase(Locale.getDefault())
        this.properties = props.toMutableList()
    }

    fun defineItSelf(): ASTStructureNode {
        val structureType = DefinedType("pointer-$name", isStructured = true)
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

    override fun toC(): String {
        return "typedef struct ${Compiler.scopePush()}{\n\t${properties.joinToString(";\n\t") { it.toC() }};\n ${Compiler.scopePop(false)}} $name;"
    }

    override fun retype(map: Map<String, DefinedType>) {
        properties.forEach{it.retype(map)}
    }

    override fun copy(): ASTStructureNode {
        return ASTStructureNode(name, properties.map { it.copy() })
    }
}