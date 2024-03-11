package compiler.types


class VoidType(): Type() {
    override fun toC() = "void";

    override fun toString(): String = "void"

    override fun copy(): Type = VoidType();

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}