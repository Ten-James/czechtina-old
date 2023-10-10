package AST

class ASTUnaryNode : ASTNode {
    var data:Any? = null

    constructor(type:String, data:Any) : super(type) {
        this.data = data
    }

    override fun toString(): String {
        if (type=="programLines") return this.data.toString();
        return "'$type', data=$data"
    }
}