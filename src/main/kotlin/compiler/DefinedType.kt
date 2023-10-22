package compiler

class DefinedType {
    val typeString: String
    val isHeap:Boolean
    var dealocated: Boolean = false

    constructor(typeString: String, isHeap: Boolean = false) {
        this.typeString = typeString
        this.isHeap = isHeap
    }

    override fun toString(): String = typeString
}