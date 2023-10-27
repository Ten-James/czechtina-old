package czechtina.lesana

import AST.ASTListNode
import AST.ASTNode
import cz.j_jzk.klang.lesana.lesana
import cz.j_jzk.klang.parse.NodeID


fun listAble (list: List<NodeID<ASTNode>>) = lesana<ASTListNode> {
    val listAble = NodeID<ASTListNode>("listAble")

    for (i in list) {
        listAble to def(i, re(","), listAble ) { ASTListNode(listOf(it.v1) + it.v3.nodes) }
        listAble to def(listAble, re(","), i ) { ASTListNode(it.v1.nodes + listOf(it.v3)) }
    }

    for (i in list) {
        for ( j in list ) {
            listAble to def(i, re(","), j ) { ASTListNode(listOf(it.v1, it.v3)) }
        }
    }
    setTopNode(listAble)
    inheritIgnoredREs()
}