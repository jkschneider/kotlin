/*
 * Copyright 2010-2012 JetBrains s.r.o.
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

package org.jetbrains.jet.lang.diagnostics;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.lang.psi.JetNamedDeclaration;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.BindingContextUtils;

import java.util.List;

/**
 * @author abreslav
 */
public interface RedeclarationDiagnostic extends Diagnostic {
    String getName();

    class SimpleRedeclarationDiagnostic extends AbstractDiagnostic<PsiElement> implements RedeclarationDiagnostic {
        private String name;

        public SimpleRedeclarationDiagnostic(@NotNull PsiElement psiElement, @NotNull String name, RedeclarationDiagnosticFactory factory) {
            super(psiElement, factory, factory.severity);
            this.name = name;
        }

        @NotNull
        @Override
        public RedeclarationDiagnosticFactory getFactory() {
            return (RedeclarationDiagnosticFactory)super.getFactory();
        }

        @NotNull
        @Override
        public String getMessage() {
            return getFactory().makeMessage(name);
        }

        @Override
        public String getName() {
            return name;
        }

        @NotNull
        @Override
        public List<TextRange> getTextRanges() {
            return POSITION_REDECLARATION.mark(getPsiElement());
        }
    }

    PositioningStrategy<PsiElement> POSITION_REDECLARATION = new PositioningStrategy<PsiElement>() {
        @NotNull
        @Override
        public List<TextRange> mark(@NotNull PsiElement element) {
            if (element instanceof JetNamedDeclaration) {
                PsiElement nameIdentifier = ((JetNamedDeclaration) element).getNameIdentifier();
                if (nameIdentifier != null) {
                    return markElement(nameIdentifier);
                }
            }
            else if (element instanceof JetFile) {
                JetFile file = (JetFile) element;
                PsiElement nameIdentifier = file.getNamespaceHeader().getNameIdentifier();
                if (nameIdentifier != null) {
                    return markElement(nameIdentifier);
                }
            }
            return markElement(element);
        }
    };
}
