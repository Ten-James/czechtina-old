package compiler


class DefinedStructure(val name:String, val type: DefinedType, var properties: Map<String, DefinedType>) {

    fun getPropType(name:String): DefinedType = properties[name] ?: throw Exception("${this.name} doesnt have type $name")

    fun addPropType(name: String, type: DefinedType) {
        if (properties.containsKey(name))
            throw Exception("${this.name} already have type $name")
        properties += mapOf(name to type)
    }
}