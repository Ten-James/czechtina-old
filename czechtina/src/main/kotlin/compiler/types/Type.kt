package compiler.types

abstract class Type {
    abstract fun toC(): String

    abstract fun copy(): Type
}