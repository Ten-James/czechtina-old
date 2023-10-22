package compiler

class DefinedFunction(val name: String,val returnType: DefinedType, val virtual: Boolean = false) {
    val variants: MutableList<DefinedFunctionVariant> = mutableListOf()

    constructor(name: String, returnType: DefinedType, variants: List<DefinedFunctionVariant>, virtual: Boolean = false) : this(name,returnType, virtual) {
        this.variants.addAll(variants)
    }

    fun validateParams(params: List<DefinedType>): Int {
        for (variant in variants) {
            if (variant.params.size != params.size){
                if (variant.enableArgs) {
                    var same = true
                    for (i in 0 until variant.params.size - 1)
                        if (variant.params[i].typeString != params[i].typeString)
                            same = false
                    if (same) {
                        variant.timeUsed++
                        return variants.indexOf(variant)
                    }
                }
            }
            if (variant.params.zip(params).all { it.first.typeString == it.second.typeString }) {
                if (variant.params.zip(params).any{!it.first.isConst && it.second.isConst} ) {
                    val newName = "${name}_v${variants.size}"
                    variants.add(DefinedFunctionVariant(newName, variant.params.zip(params).map { DefinedType(it.first.typeString,it.first.isHeap, it.first.isConst || it.second.isConst) }, defined = false, virtual = true))
                    variants.last().timeUsed++
                }

                variant.timeUsed++
                return variants.indexOf(variant)
            }
        }
        for (variant in variants) {
            val retypeMap = mutableMapOf<String, DefinedType>()
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
        throw Exception("Function $name with params ${params.joinToString(",")} not found")
    }

    override fun toString(): String = "\n$name: $returnType $virtual (\n\t${variants.joinToString("\n\t")}\n)"
}

class DefinedFunctionVariant(val translatedName: String, val params: List<DefinedType>, var defined:Boolean = true, val enableArgs: Boolean = false, val virtual: Boolean = false) {
    var timeUsed: Int = 0

    override fun toString(): String = "$translatedName(${params.joinToString(",")}): $timeUsed $defined"
}