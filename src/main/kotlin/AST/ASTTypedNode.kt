package AST


abstract class ASTTypedNode(var expType: String) : ASTNode() {

    fun definedVariable(varName: String) : String {
        if (expType.contains("array")) {
            val splited = expType.split("-")
            return "${splited[1]} ${varName}[${splited[2]}]"
        }
        if (expType == "pointer") {
            return "void* ${varName}"
        }
        return "${expType} ${varName}"
    }

    open fun getType(): String = expType

}
