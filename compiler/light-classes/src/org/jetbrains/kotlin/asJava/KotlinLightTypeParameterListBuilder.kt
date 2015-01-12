/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package org.jetbrains.kotlin.asJava

import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.plugin.JetLanguage
import com.intellij.psi.impl.light.LightTypeParameterListBuilder
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.ResolveState
import com.intellij.psi.PsiElement

public class KotlinLightTypeParameterListBuilder(manager: PsiManager): LightTypeParameterListBuilder(manager, JetLanguage.INSTANCE) {
    override fun processDeclarations(processor: PsiScopeProcessor, state: ResolveState, lastParent: PsiElement?, place: PsiElement): Boolean {
        return getTypeParameters().all { processor.execute(it, state) }
    }
}