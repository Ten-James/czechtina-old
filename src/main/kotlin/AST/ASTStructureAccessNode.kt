package AST

import compiler.Compiler
import compiler.DefinedType
import utils.Printer

class ASTStructureAccessNode: ASTVariableNode {
    var struct:ASTVariableNode
    var prop:ASTVariableNode

    constructor(struct:ASTNode, prop:ASTVariableNode) : super((struct as ASTVariableNode).data, DefinedType("")) {
        this.struct = struct
        this.prop = prop
    }

    override fun getType(): DefinedType {
        val struct = Compiler.definedStructures[struct.getType().getPrimitive()]
            ?: return DefinedType("none")

        if (struct.functions.contains(prop.data))
            return Compiler.definedFunctions["${struct.name}_${prop.data}"]!!.getReturnType(0)

        return struct.getPropType(prop.data)
    }

    fun getFunctionName(): String {
        val struct = Compiler.definedStructures[struct.getType().getPrimitive()]
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
    override fun retype(map: Map<String, DefinedType>) {
        struct.retype(map)
        prop.retype(map)
    }

    override fun addType(type: DefinedType): ASTVariableNode {
        prop.addType(type)
        return this
    }

    override fun toString(): String {
        return "Struct ACCESS, \narray=${struct.toString().replace("\n","\n\t")}, \nindex=${prop.toString().replace("\n","\n\t")}\n"
    }

    override fun toC(sideEffect:Boolean): String {
        if (!struct.getType().isStructured)
            throw Exception("Cant access to non struct type ${struct.getType()}")

        if (Compiler.definedStructures[struct.getType().getPrimitive()]!!.functions.contains("${struct.getType().getPrimitive()}_${prop.data}"))
            return "${struct.getType().getPrimitive()}_${prop.data}(${struct.toC()})"

        return "${struct.toC(false)}->${prop.toC(false)}"
    }
}