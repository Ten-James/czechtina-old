package compiler

class DefinedType {
    val typeString: String
    val isHeap:Boolean
    val isConst:Boolean
    var dealocated: Boolean = false

    constructor(typeString: String, isHeap: Boolean = false, isConst: Boolean = false) {
        this.typeString = typeString
        this.isHeap = isHeap
        this.isConst = isConst
    }

    constructor(DT: DefinedType) {
        this.typeString = DT.typeString
        this.isHeap = DT.isHeap
        this.isConst = DT.isConst
    }

    fun toHeap(): DefinedType {
        return DefinedType(typeString, true)
    }

    fun toConst(): DefinedType {
        return DefinedType(typeString, isHeap, true)
    }

    fun toPointer(): DefinedType {
        return DefinedType("pointer-$typeString", isHeap)
    }

    fun toDynamic(): DefinedType {
        if (!isHeap)
            throw Exception("Cannot convert non-heap type to dynamic")
        return DefinedType(typeString.replace("pointer","dynamic"), isHeap)
    }

    fun isPointer(): Boolean {
        return typeString.contains("pointer")
    }

    fun isDynamic(): Boolean {
        return typeString.contains("dynamic")
    }

    fun isTemplate(): Boolean {
        return typeString.contains("*")
    }
    fun getTemplate(): String {
        if (!isTemplate())
            throw Exception("Type $typeString is not template")
        if (typeString.contains("-"))
            return typeString.split("-")[1]
        return typeString
    }


    override fun toString(): String = "$typeString - $isHeap - $isConst"
}