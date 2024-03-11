package compiler.types


class PrimitiveType(private val name: String): Type() {
    override fun toC() = name;

    override fun toString(): String = "Primitive<$name>"

    override fun copy(): Type = PrimitiveType(name);

    override fun equals(other: Any?): Boolean {
        if (other is PrimitiveType)
            return name == other.name
        return super.equals(other)
    }
}