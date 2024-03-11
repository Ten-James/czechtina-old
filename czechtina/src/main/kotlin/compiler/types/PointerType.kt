package compiler.types


open class PointerType(private val addressing: Type): Type() {

    fun toDereference() = addressing;
    override fun toC() = "${addressing.toC()}*";

    override fun toString(): String = "Pointer<$addressing>"

    override fun copy(): Type = PointerType(addressing.copy());

    override fun equals(other: Any?): Boolean {
        if (other is PointerType)
            return addressing == other.addressing
        return super.equals(other)
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