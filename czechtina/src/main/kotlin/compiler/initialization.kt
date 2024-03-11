package compiler

import compiler.types.PointerType
import compiler.types.PrimitiveType
import compiler.types.VoidType

fun initDefinedFunction() = mutableMapOf(
    "printf" to DefinedFunction(
        "printf",
        VoidType(),
        listOf(DefinedFunctionVariant("printf", listOf(PrimitiveType("string")), enableArgs = true)),
        virtual = true
    ),
    "print" to DefinedFunction(
        "print",
        VoidType(),
        listOf(),
        virtual = true
    ),
    "scanf" to DefinedFunction(
        "scanf",
        VoidType(),
        listOf(DefinedFunctionVariant("scanf", listOf(PrimitiveType("string")), enableArgs = true)),
        virtual = true
    ),
)