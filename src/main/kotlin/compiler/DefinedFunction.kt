package compiler

class DefinedFunction(val name: String,val returnType: DefinedType, val virtual: Boolean = false) {
    val variants: MutableList<DefinedFunctionVariant> = mutableListOf()

    constructor(name: String, returnType: DefinedType, variants: List<DefinedFunctionVariant>, virtual: Boolean = false) : this(name,returnType, virtual) {
        this.variants.addAll(variants)
    }

    fun validateParams(params: List<String>): Int {
        println("$name: Validating params ${params.joinToString(",")}")
        for (variant in variants) {
            if (variant.params.size != params.size){
                if (variant.enableArgs) {
                    var same = true
                    for (i in 0 until variant.params.size - 1)
                        if (variant.params[i] != params[i])
                            same = false
                    if (same) {
                        variant.timeUsed++
                        return variants.indexOf(variant)
                    }
                }
            }
            if (variant.params.zip(params).all { it.first == it.second }) {
                variant.timeUsed++
                return variants.indexOf(variant)
            }
        }
        for (variant in variants) {
            val retypeMap = mutableMapOf<String, String>()
            for (i in 0 until variant.params.size) {
                val old = variant.params[i]
                if (old.contains("pointer")){
                    val newOld = old.replace("pointer", "dynamic")
                    if (newOld == params[i]) {
                        retypeMap[old] = newOld
                        continue
                    }
                }
                if (!old.contains("*"))
                    continue
                if (old.contains("-"))
                    retypeMap[old.split("-")[1]] = params[i]
                else
                    retypeMap[old] = params[i]
            }
            if (variant.params.zip(params).all { it.first == it.second || retypeMap.containsKey(it.first) && retypeMap[it.first] == it.second }) {
                val newName = "${name}_v${variants.size}"
                variants.add(DefinedFunctionVariant(newName, params, defined = false))
                println("$name: added variant ${variants.last().params.joinToString(",")}")
                variants.last().timeUsed++
                return variants.size-1
            }
        }
        throw Exception("Function $name with params ${params.joinToString(",")} not found")
    }

    override fun toString(): String = "\n$name: $returnType $virtual (\n\t${variants.joinToString("\n\t")}\n)"
}

class DefinedFunctionVariant(val translatedName: String, val params: List<String>, var defined:Boolean = true, val enableArgs: Boolean = false) {
    var timeUsed: Int = 0

    override fun toString(): String = "$translatedName(${params.joinToString(",")}): $timeUsed $defined"
}