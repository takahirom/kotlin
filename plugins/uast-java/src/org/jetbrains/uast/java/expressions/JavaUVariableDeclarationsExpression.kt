package org.jetbrains.uast.java

import org.jetbrains.uast.UElement
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.UVariableDeclarationsExpression

class JavaUVariableDeclarationsExpression(
        override val containingElement: UElement?
) : UVariableDeclarationsExpression {
    override lateinit var variables: List<UVariable>
        internal set
}