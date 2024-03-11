package compiler.types



fun isCastAbleTo(cast: Type, to: Type) : Boolean {
    if (cast == to)
        return true
    if (to is DynamicPointerType && cast !is DynamicPointerType)
        return false
    if (to !is DynamicPointerType && cast is DynamicPointerType)
        return false
    if (to is PointerType && cast is PointerType)
        return isCastAbleTo(cast.toDereference(), to.toDereference())
    return false
}