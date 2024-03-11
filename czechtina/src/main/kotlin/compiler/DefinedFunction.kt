package compiler

import compiler.types.Type
import compiler.types.isCastAbleTo

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
        /*
        for (variant in variants) {
            val retypeMap = mutableMapOf<String, Type>()
            for (i in 0 until variant.params.size) {
                val old = variant.params[i]
                if (old.isPointer()){

                    if (old.typeString.replace("pointer","dynamic") == params[i].typeString) {
                        retypeMap[old.typeString] = params[i]
                        continue
                    }
                }
                if (!old.isTemplate())
                    continue
                retypeMap[old.getTemplate()] = params[i]
            }
            if (variant.params.zip(params).all { it.first.typeString == it.second.typeString || retypeMap.containsKey(it.first.typeString) && retypeMap[it.first.typeString]!!.typeString == it.second.typeString }) {
                val newName = "${name}_v${variants.size}"
                variants.add(DefinedFunctionVariant(newName, params, defined = false))
                variants.last().timeUsed++
                return variants.size-1
            }
        }
        */
        throw Exception("Function $name with params ${params.joinToString(",")} in ${variants} not found")
    }

    override fun toString(): String = "\n$name: $returnType $virtual (\n\t${variants.joinToString("\n\t")}\n)"
}

class DefinedFunctionVariant(val translatedName: String, val params: List<Type>, val returnType: Type? = null, var defined:Boolean = true, val enableArgs: Boolean = false, val virtual: Boolean = false) {
    var timeUsed: Int = 0

    override fun toString(): String = "$translatedName(${params.joinToString(",")}): $timeUsed $defined"
}