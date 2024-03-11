package AST

import compiler.Compiler
import Printer
import compiler.types.InvalidType
import compiler.types.StructureType
import compiler.types.Type

class ASTStructureAccessNode: ASTVariableNode {
    var struct:ASTVariableNode
    var prop:ASTVariableNode

    constructor(struct:ASTNode, prop:ASTVariableNode) : super((struct as ASTVariableNode).data, InvalidType()) {
        this.struct = struct
        this.prop = prop
    }

    override fun getType(): Type {
        val struct = Compiler.definedStructures[(struct.getType() as StructureType).funcName()]
            ?: return InvalidType()

        if (struct.functions.contains(prop.data))
            return Compiler.definedFunctions["${struct.name}_${prop.data}"]!!.getReturnType(0)

        return struct.getPropType(prop.data)
    }

    fun getFunctionName(): String {
        val struct = Compiler.definedStructures[(struct.getType() as StructureType).funcName()]
            ?: throw Exception("Cant access to non struct type")

        Printer.info("struct: $struct")

        if (struct.functions.contains("${struct.name}_${prop.data}"))
            return "${struct.name}_${prop.data}"

        Printer.err("Cant Find Function ${prop.data} in ${struct.name}")

        throw Exception("Cant access to non function type")
    }

    override fun copy(): ASTStructureAccessNode {
        return ASTStructureAccessNode(struct.copy(), prop.copy())
    }
    override fun retype(map: Map<Type, Type>) {
        struct.retype(map)
        prop.retype(map)
    }

    override fun addType(type: Type): ASTVariableNode {
        prop.addType(type)
        return this
    }

    override fun toString(): String {
        return "Struct ACCESS, \narray=${struct.toString().replace("\n","\n  ")}, \nindex=${prop.toString().replace("\n","\n  ")}\n"
    }

    override fun toC(sideEffect:Boolean): String {
        val type = struct.getType()
        if (type !is StructureType)
            throw Exception("Cant access to non struct type $type")

        if (Compiler.definedStructures[type.funcName()]!!.functions.contains("${type.funcName()}_${prop.data}"))
            return "${type.funcName()}_${prop.data}(${struct.toC()})"

        return "${struct.toC(false)}->${prop.toC(false)}"
    }
}