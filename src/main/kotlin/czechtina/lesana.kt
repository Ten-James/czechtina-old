package czechtina

import AST.ASTNode
import AST.ASTOperandNode
import AST.ASTUnaryNode
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
            "literal",
            it.v1
        )
    }
    literals to def(include(decimal(allowEmptyIntegerPart = false))) { ASTUnaryNode("literal", it.v1) }
    literals to def(include(boolean())) { ASTUnaryNode("literal", it.v1) }
    literals to def(re("'.'")) { ASTUnaryNode("literal", it.v1) }
    literals to def(include(string())) { ASTUnaryNode("literal", it.v1) }
    inheritIgnoredREs()
    setTopNode(literals)
}

fun r_expression(variables: NodeID<ASTUnaryNode>) = lesana<ASTNode> {
    val literals = include(literals())
    val exp1 = NodeID<ASTNode>("expressions")
    val exp2 = NodeID<ASTNode>("expressions")
    val exp3 = NodeID<ASTNode>("expressions")

    exp1 to def(variables) { it.v1 }
    exp1 to def(literals) { it.v1 }

    exp2 to def(exp2, re("[*/%]"), exp1) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp2 to def(exp1) { it.v1 }

    exp3 to def(exp3, re("[+\\-]"), exp2) { (e1, o, e2) -> ASTOperandNode(o, e1, e2) }
    exp3 to def(exp2) { it.v1 }

    inheritIgnoredREs()
    setTopNode(exp3)
}

