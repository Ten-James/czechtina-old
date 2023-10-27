package AST

import compiler.Compiler
import compiler.DefinedType

class ASTStructureAccessNode: ASTVariableNode {
    var struct:ASTVariableNode
    var prop:ASTVariableNode

    constructor(struct:ASTNode, prop:ASTVariableNode) : super((struct as ASTVariableNode).data, DefinedType("")) {
        this.struct = struct
        this.prop = prop
    }

    override fun getType(): DefinedType {
        return Compiler.definedStructures[struct.getType().getPrimitive()]?.getPropType(prop.data) ?: DefinedType("none")
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
            throw Exception("Cant access to non struct type")
        return "${struct.toC(false)}->${prop.toC(false)}"
    }
}