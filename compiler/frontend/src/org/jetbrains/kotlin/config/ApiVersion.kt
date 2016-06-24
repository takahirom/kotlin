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

package org.jetbrains.kotlin.config

class ApiVersion private constructor(private val versionComponents: List<Int>) : Comparable<ApiVersion> {
    val versionString: String
        get() = versionComponents.joinToString(separator = ".")

    override fun compareTo(other: ApiVersion): Int {
        val a = versionComponents
        val b = other.versionComponents

        // TODO (!)
        var i = 0
        while (i < a.size && i < b.size) {
            val c = a[i].compareTo(b[i])
            if (c != 0) return c
            i++
        }

        return when {
            i == a.size && i == b.size -> 0
            i == a.size -> -1
            else -> 1
        }
    }

    override fun equals(other: Any?) =
            (other as? ApiVersion)?.versionComponents == versionComponents

    override fun hashCode() =
            versionComponents.hashCode()

    override fun toString() = versionString

    companion object {
        @JvmField
        val LATEST: ApiVersion = createByLanguageVersion(LanguageVersion.LATEST)

        @JvmStatic
        fun createByLanguageVersion(version: LanguageVersion): ApiVersion = parse(version.versionString)!!

        fun parse(versionString: String): ApiVersion? {
            return try {
                // TODO (!)
                val components = versionString.split('.').map(String::toInt).dropLastWhile(0::equals)
                ApiVersion(components)
            }
            catch (e: NumberFormatException) {
                null
            }
        }
    }
}
