package compiler

class DefinedType {
    val typeString: String
    var isHeap:Boolean

    constructor(typeString: String, isHeap: Boolean) {
        this.typeString = typeString
        this.isHeap = isHeap
    }

    override fun toString(): String = typeString
}