package compiler.types

class InvalidType: Type(){
    override fun toC(): String = "void*"
    override fun toString(): String = "InvalidType"

    override fun copy(): Type = InvalidType()
}