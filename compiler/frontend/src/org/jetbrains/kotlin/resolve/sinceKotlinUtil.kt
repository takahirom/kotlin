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

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName

private val SINCE_KOTLIN_FQ_NAME = FqName("kotlin.SinceKotlin")

/**
 * @return the value of the [SinceKotlin] annotation argument if [descriptor] should not be accessible, or null if it should be
 */
internal fun LanguageVersionSettings.getSinceVersionIfInaccessible(descriptor: DeclarationDescriptor): ApiVersion? {
    // If there's no @Since annotation or its value is not recognized, allow the access
    val version = descriptor.getSinceKotlinVersion() ?: return null

    // Otherwise allow the access iff the version in @Since is not greater than our API version
    return if (apiVersion < version) version else null
}

// TODO: doc
internal fun DeclarationDescriptor.getSinceKotlinVersion(): ApiVersion? {
    // TODO: property accessors
    // TODO: use-site targets
    // TODO: overrides
    val my = getOwnSinceKotlinAnnotationVersion()?.let(ApiVersion.Companion::parse)
    val ctorClass = (this as? ConstructorDescriptor)?.containingDeclaration?.getSinceKotlinVersion()
    return listOfNotNull(my, ctorClass).min()
}

private fun DeclarationDescriptor.getOwnSinceKotlinAnnotationVersion(): String? =
        annotations.findAnnotation(SINCE_KOTLIN_FQ_NAME)?.allValueArguments?.values?.singleOrNull()?.value as? String
