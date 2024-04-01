package compiler.types



fun isCastAbleTo(cast: Type, to: Type) : Boolean {
    Printer.lowInfo("Trying Casting $cast to $to")
    if (to is DynamicPointerType && cast !is DynamicPointerType)
        return false
    if (to !is DynamicPointerType && cast is DynamicPointerType)
        return false
    if (cast == to)
        return true
    if (to is PointerType && cast is PointerType)
        return isCastAbleTo(cast.toDereference(), to.toDereference())
    return false
}

fun getGenericType(type: Type) : Type {
    if (type is GenericType)
        return type
    if (type is PointerType)
        return getGenericType(type.toDereference())
    return VoidType()
}

fun isGenericCastAble(cast: Type, to: Type) : Pair<Type,Type>? {
    if (to is GenericType)
        return Pair(cast, to)
    if (to is PointerType && cast is PointerType) {
        return isGenericCastAble(cast.toDereference(), to.toDereference())
    }
    return null
}