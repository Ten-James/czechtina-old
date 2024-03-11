package compiler

/*class Type {
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

    constructor(DT: Type) {
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

    fun toArray(size: String): Type {
        if (isPointer() || isDynamic())
            return changeTypeString("array-${this.typeString}-${size}")
        return changeTypeString("array-${this.typeString}-${size}")
    }

    fun toHeap(): Type = Type(typeString, true, isConst, isStructured)
    fun toConst(): Type = Type(typeString, isHeap, true, isStructured)
    fun toPointer(): Type = changeTypeString("pointer-$typeString")


    fun toDereference(): Type {
        if (!isAddress() && Compiler.isParsed)
            throw Exception("Cannot convert non-pointer type to dereference - $this")
        if (isAddress())
            return changeTypeString(typeString.substring(typeString.indexOf("-")+1))
        return changeTypeString("none")
    }
    fun toDynamic(): Type {
        if (!isHeap && Compiler.isParsed) {
            println(this)
            throw Exception("Cannot convert non-heap type to dynamic")
        }
        return changeTypeString(typeString.replace("pointer","dynamic"))
    }


    fun changeTypeString(newTypeString: String):Type {
        var newT = Type(this)
        newT.typeString = newTypeString
        return newT
    }

    fun isAddress() = isPointer() || isDynamic() || isArray()
    fun isPointer() = typeString.contains("pointer")
    fun isDynamic() = typeString.contains("dynamic")
    fun isArray() = typeString.contains("array")

    fun isTemplate(): Boolean = typeString.contains("*")

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
        if (typeString.contains("-")) {
            val arr = typeString.split("-")
            for (i in arr)
                if (i != "pointer" && i != "dynamic" && i != "array")
                    return i
        }
        return typeString
    }


    override fun toString(): String = "$typeString ${if (isHeap) "Heap " else ""}${if (isConst) "Const " else ""}${if (isStructured) "Struct" else " "}"

    fun unDynamic(): Type {
        if (isDynamic())
            return changeTypeString(typeString.replace("dynamic","pointer"))
        return Type(this)
    }


    fun isCastAbleTo(Type: Type) : Boolean {
        if (Type.typeString == typeString)
            return true
        if (Type.isDynamic() && !isDynamic())
            return false
        if (!Type.isDynamic() && isDynamic())
            return false
        if (Type.isAddress() && isAddress())
            return Type.getPrimitive() == getPrimitive()
        return false
    }
}*/