package compiler

import Printer
import compiler.types.*

class DefinedFunction(val name: String,val returnType: Type, val virtual: Boolean = false) {
    val variants: MutableList<DefinedFunctionVariant> = mutableListOf()

    constructor(name: String, returnType: Type, variants: List<DefinedFunctionVariant>, virtual: Boolean = false) : this(name,returnType, virtual) {
        this.variants.addAll(variants)
    }

    fun getReturnType(variantIndex : Int): Type {
        return variants[variantIndex].returnType ?: returnType
    }

    fun validateParams(params: List<Type>): Int {
        for (variant in variants) {
            if (variant.params.size != params.size){
                if (variant.enableArgs) {
                    var same = true
                    for (i in 0 until variant.params.size - 1) {
                        if (variant.params[i] != params[i])
                            same = false
                    }
                    if (same) {
                        variant.timeUsed++
                        return variants.indexOf(variant)
                    }
                }
            }
            if (variant.params.zip(params).all { isCastAbleTo(it.second,it.first)}) {

                variant.timeUsed++
                return variants.indexOf(variant)
            }
        }

        for (variant in variants) {
            if (variant.virtual)
                continue
            val map = mutableMapOf<Type, Type>()
            println(params)
            println(variant.params)
            if (variant.params.zip(params).all {
                if (isCastAbleTo(it.second, it.first)) {
                    true
                } else {
                    if (it.second is DynamicPointerType && it.first is PointerType) {
                        true
                    } else if (it.first.isGeneric() && isGenericCastAble(it.second, it.first) != null) {
                        val pair = isGenericCastAble(it.second, it.first)!!
                        if (map.containsKey(pair.second)) {
                            pair.first == map[pair.second]
                        } else {
                            map[pair.second] = pair.first
                            true
                        }
                    }
                    else {
                        false
                    }
                }
                }) {
                val newName = "${name}_v${variants.size}"
                Printer.info("--------------")
                Printer.info(map.toString())
                Printer.info("--------------")
                val newReturnType = if (returnType.isGeneric() && map.containsKey(returnType)) {
                    map[returnType]
                } else {
                    returnType
                }
                variants.add(DefinedFunctionVariant(newName, params, newReturnType, map, false, false, false))
                variants[variants.size - 1].timeUsed = 1
                return variants.size - 1
            }
        }
        throw Exception("Function $name with params ${params.joinToString(",")} in $variants not found")
    }

    override fun toString(): String = "\n$name: $returnType ${if (virtual) "Virtual" else ""} (\n\t${variants.joinToString("\n\t")}\n)"
}

class DefinedFunctionVariant(val translatedName: String, val params: List<Type>, val returnType: Type? = null, val retypeMap: Map<Type, Type> = mapOf(), var defined:Boolean = true, val enableArgs: Boolean = false, val virtual: Boolean = false) {
    var timeUsed: Int = 0

    override fun toString(): String = "$translatedName(${params.joinToString(",")}): $timeUsed ${if (defined) "Defined" else ""} ${if (returnType !== null) returnType.toString() else ""} ${if (virtual) "Virtual" else ""} ${if (enableArgs) "Enable Args" else ""}"
}