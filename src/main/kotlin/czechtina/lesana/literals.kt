package czechtina.lesana
import AST.ASTUnaryNode
import AST.ASTUnaryTypes
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID
import cz.j_jzk.klang.prales.constants.boolean
import cz.j_jzk.klang.prales.constants.decimal
import cz.j_jzk.klang.prales.constants.integer
import cz.j_jzk.klang.prales.constants.string


fun literals() = lesana<ASTUnaryNode> {
    val literals = NodeID<ASTUnaryNode>("literal")

    literals to def(include(integer(nonDecimal = false, underscoreSeparation = false))) {
        ASTUnaryNode(
            ASTUnaryTypes.LITERAL,
            it.v1
        )
    }
    literals to def(include(decimal(allowEmptyIntegerPart = false))) { ASTUnaryNode(ASTUnaryTypes.LITERAL, it.v1) }
    literals to def(include(boolean())) { ASTUnaryNode(ASTUnaryTypes.LITERAL, it.v1) }
    literals to def(re("'.'")) { ASTUnaryNode(ASTUnaryTypes.LITERAL, it.v1) }
    literals to def(include(string())) { ASTUnaryNode(ASTUnaryTypes.STRING, it.v1) }
    inheritIgnoredREs()
    setTopNode(literals)
}


