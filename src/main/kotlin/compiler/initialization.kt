package compiler

fun initDefinedFunction() = mutableMapOf(
    "printf" to DefinedFunction(
        "printf",
        DefinedType("void"),
        listOf(DefinedFunctionVariant("printf", listOf(DefinedType("string")), enableArgs = true)),
        virtual = true
    ),
    "scanf" to DefinedFunction(
        "scanf",
        DefinedType("scanf"),
        listOf(DefinedFunctionVariant("scanf", listOf(DefinedType("string")), enableArgs = true)),
        virtual = true
    ),
    "new" to DefinedFunction(
        "new", DefinedType("dynamic-void", true), listOf(
            DefinedFunctionVariant(
                "malloc", listOf(
                    DefinedType("int")
                )
            )
        ), virtual = true
    ),
    "predej" to DefinedFunction(
        "predej", DefinedType("pointer-void"), listOf(
            DefinedFunctionVariant(
                "", listOf(
                    DefinedType("pointer", isHeap = true)
                )
            )
        ), virtual = true
    ),
    "const" to DefinedFunction(
        "const", DefinedType("pointer-void"), listOf(
            DefinedFunctionVariant(
                "", listOf(
                    DefinedType("pointer")
                )
            )
        ), virtual = true
    ),
    "throw" to DefinedFunction(
        "throw",
        DefinedType("void"),
        listOf(DefinedFunctionVariant("throw", listOf(DefinedType("string")), enableArgs = true)),
        virtual = true
    ),
)