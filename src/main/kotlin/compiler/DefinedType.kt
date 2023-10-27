package compiler

class DefinedType {
    var typeString: String
    val isHeap:Boolean
    val isConst:Boolean
    val isStructured:Boolean
    var dealocated: Boolean = false

    constructor(typeString: String, isHeap: Boolean = false, isConst: Boolean = false, isStructured: Boolean = false) {
        this.typeString = typeString
        this.isHeap = isHeap
        this.isConst = isConst
        this.isStructured = isStructured
    }

    constructor(DT: DefinedType) {
        this.typeString = DT.typeString
        this.isHeap = DT.isHeap
        this.isConst = DT.isConst
        this.isStructured = DT.isStructured
    }

    fun toC(): String {
        if (isPointer() || isDynamic())
            return "${getPrimitive()} *"
        return getPrimitive()
    }

    fun toArray(size: Number): DefinedType {
        if (isPointer() || isDynamic())
            return DefinedType("array-${getPrimitive()}-${size}", isHeap, isConst)
        return DefinedType("array-${getPrimitive()}-${size}", isHeap, isConst)
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
    fun toDereference(): DefinedType {
        if (!isAddress() && Compiler.isParsed)
            throw Exception("Cannot convert non-pointer type to dereference - $this")
        if (isAddress())
            return DefinedType(typeString.substring(typeString.indexOf("-")+1), isHeap, isConst)
        return DefinedType("none", isHeap)
    }

    fun toDynamic(): DefinedType {
        if (!isHeap)
            throw Exception("Cannot convert non-heap type to dynamic")
        return DefinedType(typeString.replace("pointer","dynamic"), isHeap, isConst, isStructured)
    }

    fun isPointer(): Boolean {
        return typeString.contains("pointer")
    }

    fun isAddress(): Boolean = isPointer() || isDynamic()

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

    fun getPrimitive(): String {
        if (isTemplate())
            return getTemplate()
        if (typeString.contains("-"))
            return typeString.split("-")[1]
        return typeString
    }


    override fun toString(): String = "$typeString - $isHeap - $isConst"

    fun unDynamic(): DefinedType {
        if (isDynamic())
            return DefinedType(typeString.replace("dynamic","pointer"), isHeap, isConst, isStructured)
        return DefinedType(typeString, isHeap, isConst, isStructured)
    }
}