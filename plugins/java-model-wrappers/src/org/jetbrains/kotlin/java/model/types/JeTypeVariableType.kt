/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.java.model.types

import org.jetbrains.kotlin.java.model.elements.JeTypeParameterElement
import com.intellij.psi.*
import org.jetbrains.kotlin.annotation.processing.impl.toDisposable
import org.jetbrains.kotlin.annotation.processing.impl.dispose
import org.jetbrains.kotlin.java.model.internal.JeElementRegistry
import org.jetbrains.kotlin.java.model.toJeElement
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.type.TypeVisitor

class JeTypeVariableType(
        psiType: PsiClassType,
        parameter: PsiTypeParameter,
        private val registry: JeElementRegistry
) : JePsiType(), JeTypeWithManager, TypeVariable {
    init { registry.register(this) }

    private val disposablePsiType = psiType.toDisposable()
    private val disposableParameter = parameter.toDisposable()

    override fun dispose() = dispose(disposablePsiType, disposableParameter)

    override val psiType: PsiType
        get() = disposablePsiType()

    val parameter: PsiTypeParameter
        get() = disposableParameter()

    override fun getKind() = TypeKind.TYPEVAR
    
    override fun <R : Any?, P : Any?> accept(v: TypeVisitor<R, P>, p: P) = v.visitTypeVariable(this, p)

    override val psiManager: PsiManager
        get() = parameter.manager

    override fun getLowerBound(): TypeMirror? {
        //TODO support captured lower bounds
        return JeNullType
    }

    override fun getUpperBound(): TypeMirror? {
        val superTypes = parameter.superTypes
        return if (superTypes.size == 1) {
            superTypes.first().toJeType(psiManager, registry)
        } else {
            PsiIntersectionType.createIntersection(*superTypes).toJeType(psiManager, registry)
        }
    }

    override fun asElement() = JeTypeParameterElement(parameter, registry, parameter.owner.toJeElement(registry))

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return psiType == (other as? JeTypeVariableType)?.psiType
    }

    override fun toString() = parameter.name ?: "<none>"

    override fun hashCode() = psiType.hashCode()
}