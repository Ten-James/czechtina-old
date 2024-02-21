package compiler


class DefinedStructure(val name:String, val type: DefinedType, var properties: Map<String, DefinedType>, var functions:MutableList<String> = mutableListOf()) {

    fun getPropType(name:String): DefinedType = properties[name] ?: throw Exception("${this.name} doesnt have type $name")

    fun addPropType(name: String, type: DefinedType) {
        if (properties.containsKey(name))
            throw Exception("${this.name} already have type $name")
        properties += mapOf(name to type)
    }

    fun addFunction(func:String) {
        if (functions.contains(func))
            throw Exception("${this.name} already have function $name")
        functions.add(func)
    }

    override fun toString(): String {
        return "DefinedStructure(name='$name', type=$type, properties=$properties, functions=$functions)"
    }
}