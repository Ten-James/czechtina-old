package compiler.types


open class PointerType(private val addressing: Type): Type() {

    fun toDereference() = addressing;
    override fun toC() = "${addressing.toC()}*";

    override fun toString(): String = "Pointer<$addressing>"

    override fun copy(): Type = PointerType(addressing.copy());

    override fun isGeneric() = addressing.isGeneric();

    override fun equals(other: Any?): Boolean {
        if (other is PointerType)
            return addressing == other.addressing
        return super.equals(other)
    }

    override fun reType(map: Map<Type, Type>): Type {
        val newAddressing = addressing.reType(map);
        if (map.containsKey(this))
            return map[this]!!
        if (newAddressing != addressing)
            return PointerType(newAddressing);
        return this;
    }
}

class  DynamicPointerType(private val addressing: Type): PointerType(addressing) {
    override fun toString(): String = "DynamicPointer<$addressing>"

    fun toUndynamic() = PointerType(addressing.copy());
    override fun copy(): Type = DynamicPointerType(addressing.copy());

}

class StaticArrayType(private val addressing: Type, private val size: String): PointerType(addressing) {

    fun getSize() = size;
    override fun toC() = "${addressing.toC()}*";
    override fun toString(): String = "StaticArray<$addressing, $size>"
    override fun copy(): Type = StaticArrayType(addressing.copy(), size);
}