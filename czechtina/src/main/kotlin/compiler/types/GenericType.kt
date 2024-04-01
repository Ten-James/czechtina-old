package compiler.types


class GenericType(private val name: String): Type() {
    override fun toC() = "void *";

    override fun toString(): String = "Generic<$name>"

    override fun copy(): Type = GenericType(name);

    override fun equals(other: Any?): Boolean {
        if (other is GenericType)
            return this.toString() == other.toString()
        return super.equals(other)
    }

    override fun reType(map: Map<Type, Type>): Type {
        if (map.containsKey(this))
            return map[this]!!
        return this
    }

    override fun isGeneric() = true
}