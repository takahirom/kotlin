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

package org.jetbrains.kotlin.idea.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.KtPsiFactory.CallableBuilder
import org.jetbrains.kotlin.psi.KtPsiFactory.CallableBuilder.Target.CONSTRUCTOR
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class ConvertPrimaryConstructorToSecondaryIntention : SelfTargetingIntention<KtPrimaryConstructor>(
        KtPrimaryConstructor::class.java,
        "Convert to secondary constructor"
) {
    override fun isApplicableTo(element: KtPrimaryConstructor, caretOffset: Int) =
            element.containingClassOrObject != null

    private fun KtParameter.users(classOrObject: KtClassOrObject): Set<KtProperty> {
        val result = linkedSetOf<KtProperty>()
        ReferencesSearch.search(this, LocalSearchScope(classOrObject)).forEach {
            val property = it.element.getParentOfType<KtProperty>(strict = true)
            if (property != null) {
                result += property
            }
        }
        return result
    }

    override fun applyTo(element: KtPrimaryConstructor, editor: Editor?) {
        val classOrObject = element.containingClassOrObject ?: return
        val factory = KtPsiFactory(classOrObject)
        val parameterUsers = element.valueParameters
                .filter { !it.hasValOrVar() }
                .map { it.users(classOrObject) }
                .fold(linkedSetOf<KtProperty>()) {
            prevUsers: Set<KtProperty>, users -> prevUsers + users
        }
        val initializerMap = mutableMapOf<KtProperty, String>()
        for (parameterUser in parameterUsers) {
            if (parameterUser.typeReference == null) {
                SpecifyTypeExplicitlyIntention().applyTo(parameterUser, editor)
            }
            val initializer = parameterUser.initializer ?: continue
            initializerMap[parameterUser] = initializer.text
            initializer.delete()
            parameterUser.equalsToken!!.delete()
        }
        val constructor = factory.createSecondaryConstructor(
                CallableBuilder(CONSTRUCTOR).apply {
                    element.modifierList?.let { modifier(it.text) }
                    typeParams()
                    name("constructor")
                    for (valueParameter in element.valueParameters) {
                        param(valueParameter.name!!, valueParameter.typeReference!!.text, valueParameter.defaultValue?.text)
                    }
                    noReturnType()
                    for (superTypeEntry in classOrObject.getSuperTypeListEntries()) {
                        if (superTypeEntry is KtSuperTypeCallEntry) {
                            superDelegation(superTypeEntry.valueArgumentList?.text ?: "")
                            superTypeEntry.valueArgumentList?.delete()
                        }
                    }
                    if (element.valueParameters.firstOrNull { it.hasValOrVar() } != null || initializerMap.isNotEmpty()) {
                        val valueParameterInitializers = element.valueParameters.filter { it.hasValOrVar() }.joinToString(separator = "\n") {
                            val name = it.name!!
                            "this.$name = $name"
                        }
                        val parameterUserInitializers = initializerMap.entries.joinToString(separator = "\n") {
                            val name = it.key.name!!
                            val text = it.value
                            "this.$name = $text"
                        }
                        blockBody(listOf(valueParameterInitializers, parameterUserInitializers)
                                          .filter(String::isNotEmpty).joinToString(separator = "\n"))
                    }
                }.asString()
        )
        classOrObject.addDeclarationBefore(constructor, null)
        for (valueParameter in element.valueParameters.reversed()) {
            if (!valueParameter.hasValOrVar()) continue
            val property = factory.createProperty(valueParameter.name!!, valueParameter.typeReference?.text, valueParameter.isMutable)
            classOrObject.addDeclarationBefore(property, null)
        }
        element.delete()
    }
}