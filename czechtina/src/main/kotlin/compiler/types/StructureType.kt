package compiler.types

open class StructureType(private val name: String): Type() {
    override fun toC() = "$name *"
    override fun toString(): String = "Structure<$name>"

    fun funcName(): String = "CZ_$name"

    fun getStructName(): String = name
    override fun copy(): Type = StructureType(name)

    override fun equals(other: Any?): Boolean {
        if (other is StructureType && other !is DynamicStructureType)
            return other.name == name
        return super.equals(other)
    }

    override fun reType(map: Map<Type, Type>): Type {
        if (map.containsKey(this))
            return map[this]!!
        return this
    }
}

class DynamicStructureType(private val name: String): StructureType(name) {
    override fun toString(): String = "DynamicStructure<$name>"

    override fun copy(): Type = DynamicStructureType(name)


    override fun equals(other: Any?): Boolean {
        if (other is DynamicStructureType)
            return other.name == name
        return super.equals(other)
    }
}