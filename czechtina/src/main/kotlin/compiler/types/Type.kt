package compiler.types

abstract class Type {
    abstract fun toC(): String

    abstract fun copy(): Type

    open fun isGeneric() = false

    abstract fun reType(map: Map<Type, Type>): Type

    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }
}