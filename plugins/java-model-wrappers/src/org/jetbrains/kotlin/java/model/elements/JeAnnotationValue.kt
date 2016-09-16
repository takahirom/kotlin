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

package org.jetbrains.kotlin.java.model.elements

import com.intellij.openapi.Disposable
import com.intellij.psi.*
import org.jetbrains.kotlin.annotation.processing.impl.toDisposable
import org.jetbrains.kotlin.annotation.processing.impl.dispose
import org.jetbrains.kotlin.java.model.internal.JeElementRegistry
import org.jetbrains.kotlin.java.model.internal.calcConstantValue
import org.jetbrains.kotlin.java.model.types.toJeType
import java.util.*
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.AnnotationValueVisitor

fun PsiAnnotationMemberValue.toJeAnnotationValue(registry: JeElementRegistry): AnnotationValue {
    val original = originalElement
    val annotationValue = when (original) {
        is PsiLiteral -> JeLiteralAnnotationValue(original, registry)
        is PsiAnnotation -> JeAnnotationAnnotationValue(original, registry)
        is PsiArrayInitializerMemberValue -> JeArrayAnnotationValue(original, registry)
        is PsiClassObjectAccessExpression -> JeTypeAnnotationValue(original, registry)
        is PsiReferenceExpression -> {
            val element = original.resolve()
            if (element is PsiEnumConstant) {
                JeEnumValueAnnotationValue(element, registry)
            } 
            else if (element is PsiField && element.hasInitializer()) {
                (element.initializer ?: error("Field should have an initializer")).toJeAnnotationValue(registry)
            }
            else {
                JeErrorAnnotationValue(this, registry)
            }
        }
        is PsiExpression -> JeExpressionAnnotationValue(original, registry)
        else -> throw AssertionError("Unsupported annotation element value: $this (original = $original)")
    }
    return annotationValue
}

interface JeAnnotationValue : AnnotationValue, Disposable

abstract class JeDisposableValue<out T : PsiElement>(psi: T, protected val registry: JeElementRegistry) : JeAnnotationValue {
    init {
        @Suppress("LeakingThis")
        registry.register(this)
    }

    private val disposablePsi = psi.toDisposable()
    override fun dispose() = dispose(disposablePsi)

    val psi: T
        get() = disposablePsi()
}

class JeAnnotationAnnotationValue(psi: PsiAnnotation, registry: JeElementRegistry) : JeDisposableValue<PsiAnnotation>(psi, registry) {
    override fun getValue() = JeAnnotationMirror(psi, registry)
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P) = v.visitAnnotation(value, p)
}

class JeEnumValueAnnotationValue(psi: PsiEnumConstant, registry: JeElementRegistry) : JeDisposableValue<PsiEnumConstant>(psi, registry) {
    override fun getValue() = JeVariableElement(psi, registry)
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P) = v.visitEnumConstant(value, p) 
}

class JeTypeAnnotationValue(psi: PsiClassObjectAccessExpression, registry: JeElementRegistry) : JeDisposableValue<PsiClassObjectAccessExpression>(psi, registry) {
    override fun getValue() = psi.operand.type.toJeType(psi.manager, registry)
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P) = v.visitType(value, p)
}

abstract class JePrimitiveAnnotationValue<T : PsiElement>(psi: T, registry: JeElementRegistry) : JeDisposableValue<T>(psi, registry) {
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P): R {
        val value = this.value
        return when (value) {
            is String -> v.visitString(value, p)
            is Int -> v.visitInt(value, p)
            is Boolean -> v.visitBoolean(value, p)
            is Char -> v.visitChar(value, p)
            is Byte -> v.visitByte(value, p)
            is Short -> v.visitShort(value, p)
            is Long -> v.visitLong(value, p)
            is Float -> v.visitFloat(value, p)
            is Double -> v.visitDouble(value, p)
            else -> throw AssertionError("Bad annotation element value: $value")
        }
    }
}

class JeLiteralAnnotationValue(psi: PsiLiteral, registry: JeElementRegistry) : JePrimitiveAnnotationValue<PsiLiteral>(psi, registry) {
    override fun getValue() = psi.value
}

class JeExpressionAnnotationValue(psi: PsiExpression, registry: JeElementRegistry) : JePrimitiveAnnotationValue<PsiExpression>(psi, registry) {
    override fun getValue() = psi.calcConstantValue()
}

class JeArrayAnnotationValue(psi: PsiArrayInitializerMemberValue, registry: JeElementRegistry) : JePrimitiveAnnotationValue<PsiArrayInitializerMemberValue>(psi, registry) {
    override fun getValue() = psi.initializers.map { it.toJeAnnotationValue(registry) }
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P) = v.visitArray(value, p)
}

class JeSingletonArrayAnnotationValue(psi: PsiAnnotationMemberValue, registry: JeElementRegistry) : JePrimitiveAnnotationValue<PsiAnnotationMemberValue>(psi, registry) {
    override fun getValue(): List<AnnotationValue> = Collections.singletonList(psi.toJeAnnotationValue(registry))
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P) = v.visitArray(value, p)
}

class JeErrorAnnotationValue(psi: PsiAnnotationMemberValue, registry: JeElementRegistry) : JePrimitiveAnnotationValue<PsiAnnotationMemberValue>(psi, registry) {
    override fun <R : Any?, P : Any?> accept(v: AnnotationValueVisitor<R, P>, p: P) = v.visitString(psi.text, p)
    override fun getValue() = null
}