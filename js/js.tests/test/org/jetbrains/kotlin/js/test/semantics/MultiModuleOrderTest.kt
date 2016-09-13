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

package org.jetbrains.kotlin.js.test.semantics

import org.jetbrains.kotlin.js.config.EcmaVersion
import org.jetbrains.kotlin.js.test.MultipleModulesTranslationTest
import org.jetbrains.kotlin.js.test.WithModuleKind
import org.jetbrains.kotlin.js.test.rhino.RhinoResultChecker
import org.jetbrains.kotlin.js.test.rhino.RhinoUtils
import org.jetbrains.kotlin.serialization.js.ModuleKind
import org.mozilla.javascript.JavaScriptException

class MultiModuleOrderTest : MultipleModulesTranslationTest("multiModuleOrder/") {
    private var overriddenTestName = ""

    @WithModuleKind(ModuleKind.PLAIN) fun testPlain() {
        runTest("simple")
    }

    @WithModuleKind(ModuleKind.UMD) fun testUmd() {
        runTest("simple")
    }

    fun runTest(name: String) {
        overriddenTestName = name
        doTest("${pathToTestDir()}/cases/$name")
        checkWrongOrderReported()
    }

    private fun checkWrongOrderReported() {
        val dirName = getTestName(true)
        val mainJsFile = getOutputFilePath(getModuleDirectoryName(dirName, "main"), EcmaVersion.v5)
        val libJsFile = getOutputFilePath(getModuleDirectoryName(dirName, "lib"), EcmaVersion.v5)
        try {
            RhinoUtils.runRhinoTest(listOf(mainJsFile, libJsFile), RhinoResultChecker { context, scope ->
                // don't check anything, expect exception from function
            })
        }
        catch (e: JavaScriptException) {
            val message = e.message!!
            assertTrue("Exception message should contain reference to dependency (lib)", "'lib'" in message)
            assertTrue("Exception message should contain reference to module that failed to load (main)", "'main'" in message)
            return
        }
        fail("Exception should have been thrown due to wrong order of modules")
    }


    override fun getTestName(lowercaseFirstLetter: Boolean) = overriddenTestName

    override fun getOutputPath() = "${super.getOutputPath()}/${moduleKind.name.toLowerCase()}/"
}