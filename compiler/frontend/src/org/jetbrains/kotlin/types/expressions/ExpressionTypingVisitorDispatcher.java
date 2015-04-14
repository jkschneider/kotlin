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

package org.jetbrains.kotlin.types.expressions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.BindingContextUtils;
import org.jetbrains.kotlin.resolve.scopes.WritableScope;
import org.jetbrains.kotlin.types.DeferredType;
import org.jetbrains.kotlin.types.ErrorUtils;
import org.jetbrains.kotlin.types.JetTypeInfo;
import org.jetbrains.kotlin.util.ReenteringLazyValueComputationException;
import org.jetbrains.kotlin.utils.KotlinFrontEndException;

import static org.jetbrains.kotlin.diagnostics.Errors.TYPECHECKER_HAS_RUN_INTO_RECURSIVE_PROBLEM;
import static org.jetbrains.kotlin.resolve.bindingContextUtil.BindingContextUtilPackage.recordScopeAndDataFlowInfo;

public class ExpressionTypingVisitorDispatcher extends JetVisitor<JetTypeInfo, ExpressionTypingContext> implements ExpressionTypingInternals {

    public interface ExpressionTypingVisitorForStatementsProvider {
        ExpressionTypingVisitorForStatements get(@NotNull ExpressionTypingContext context);
    }

    public class ExpressionTypingVisitorForStatementsProviderForBlock implements ExpressionTypingVisitorForStatementsProvider {
        private final ExpressionTypingVisitorForStatements visitorForBlock;


        public ExpressionTypingVisitorForStatementsProviderForBlock(@NotNull WritableScope scope) {
            visitorForBlock = new ExpressionTypingVisitorForStatements(
                    ExpressionTypingVisitorDispatcher.this, scope, basic, controlStructures, patterns, functions
            );
        }

        @Override
        public ExpressionTypingVisitorForStatements get(@NotNull ExpressionTypingContext context) {
            return visitorForBlock;
        }
    }

    public class ExpressionTypingVisitorForStatementsProviderForDeclarations implements ExpressionTypingVisitorForStatementsProvider {
        @Override
        public ExpressionTypingVisitorForStatements get(@NotNull ExpressionTypingContext context) {
            return createStatementVisitor(context);
        }
    }

    private static final Logger LOG = Logger.getInstance(ExpressionTypingVisitor.class);

    @NotNull
    public static ExpressionTypingFacade create(@NotNull ExpressionTypingComponents components) {
        ExpressionTypingVisitorDispatcher typingVisitorDispatcher = new ExpressionTypingVisitorDispatcher(components);
        typingVisitorDispatcher.setProviderForStatements(typingVisitorDispatcher.new ExpressionTypingVisitorForStatementsProviderForDeclarations());
        return typingVisitorDispatcher;
    }

    @NotNull
    public static ExpressionTypingInternals createForBlock(
            @NotNull ExpressionTypingComponents components,
            @NotNull WritableScope writableScope
    ) {
        ExpressionTypingVisitorDispatcher typingVisitorDispatcher = new ExpressionTypingVisitorDispatcher(components);
        typingVisitorDispatcher.setProviderForStatements(typingVisitorDispatcher.new ExpressionTypingVisitorForStatementsProviderForBlock(writableScope));
        return typingVisitorDispatcher;

    }

    private final ExpressionTypingComponents components;
    private final BasicExpressionTypingVisitor basic;
    private ExpressionTypingVisitorForStatementsProvider providerForStatements;
    private final FunctionsTypingVisitor functions;
    private final ControlStructureTypingVisitor controlStructures;
    private final PatternMatchingTypingVisitor patterns;

    public void setProviderForStatements(ExpressionTypingVisitorForStatementsProvider providerForStatements) {
        this.providerForStatements = providerForStatements;
    }

    private ExpressionTypingVisitorDispatcher(
            @NotNull ExpressionTypingComponents components
    ) {
        this.components = components;
        this.basic = new BasicExpressionTypingVisitor(this);
        this.controlStructures = new ControlStructureTypingVisitor(this);
        this.patterns = new PatternMatchingTypingVisitor(this);
        this.functions = new FunctionsTypingVisitor(this);
    }

    @Override
    @NotNull
    public ExpressionTypingComponents getComponents() {
        return components;
    }

    @NotNull
    @Override
    public JetTypeInfo checkInExpression(
            @NotNull JetElement callElement,
            @NotNull JetSimpleNameExpression operationSign,
            @NotNull ValueArgument leftArgument,
            @Nullable JetExpression right,
            @NotNull ExpressionTypingContext context
    ) {
        return basic.checkInExpression(callElement, operationSign, leftArgument, right, context);
    }

    @Override
    @NotNull
    public final JetTypeInfo safeGetTypeInfo(@NotNull JetExpression expression, ExpressionTypingContext context) {
        JetTypeInfo typeInfo = getTypeInfo(expression, context);
        if (typeInfo.getType() != null) {
            return typeInfo;
        }
        return JetTypeInfo.create(ErrorUtils.createErrorType("Type for " + expression.getText()), context.dataFlowInfo);
    }

    @Override
    @NotNull
    public final JetTypeInfo getTypeInfo(@NotNull JetExpression expression, ExpressionTypingContext context) {
        return getTypeInfo(expression, context, this);
    }

    @Override
    @NotNull
    public final JetTypeInfo getTypeInfo(@NotNull JetExpression expression, ExpressionTypingContext context, boolean isStatement) {
        if (!isStatement) return getTypeInfo(expression, context);
        return getTypeInfo(expression, context, providerForStatements.get(context));
    }
    
    private ExpressionTypingVisitorForStatements createStatementVisitor(ExpressionTypingContext context) {
        return new ExpressionTypingVisitorForStatements(this,
                                                        ExpressionTypingUtils.newWritableScopeImpl(context, "statement scope"),
                                                        basic, controlStructures, patterns, functions);
    }

    @Override
    public void checkStatementType(@NotNull JetExpression expression, ExpressionTypingContext context) {
        expression.accept(createStatementVisitor(context), context);
    }

    @NotNull
    private JetTypeInfo getTypeInfo(@NotNull JetExpression expression, ExpressionTypingContext context, JetVisitor<JetTypeInfo, ExpressionTypingContext> visitor) {
        try {
            JetTypeInfo recordedTypeInfo = BindingContextUtils.getRecordedTypeInfo(expression, context.trace.getBindingContext());
            if (recordedTypeInfo != null) {
                return recordedTypeInfo;
            }
            JetTypeInfo result;
            try {
                result = expression.accept(visitor, context);
                // Some recursive definitions (object expressions) must put their types in the cache manually:
                if (context.trace.get(BindingContext.PROCESSED, expression)) {
                    return JetTypeInfo.create(context.trace.getBindingContext().get(BindingContext.EXPRESSION_TYPE, expression),
                                              result.getDataFlowInfo());
                }

                if (result.getType() instanceof DeferredType) {
                    result = JetTypeInfo.create(((DeferredType) result.getType()).getDelegate(), result.getDataFlowInfo());
                }
                if (result.getType() != null) {
                    context.trace.record(BindingContext.EXPRESSION_TYPE, expression, result.getType());
                }

            }
            catch (ReenteringLazyValueComputationException e) {
                context.trace.report(TYPECHECKER_HAS_RUN_INTO_RECURSIVE_PROBLEM.on(expression));
                result = JetTypeInfo.create(null, context.dataFlowInfo);
            }

            context.trace.record(BindingContext.PROCESSED, expression);
            recordScopeAndDataFlowInfo(context.replaceDataFlowInfo(result.getDataFlowInfo()), expression);
            return result;
        }
        catch (ProcessCanceledException e) {
            throw e;
        }
        catch (KotlinFrontEndException e) {
            throw e;
        }
        catch (Throwable e) {
            context.trace.report(Errors.EXCEPTION_FROM_ANALYZER.on(expression, e));
            LOG.error(
                    "Exception while analyzing expression at " + DiagnosticUtils.atLocation(expression) + ":\n" + expression.getText() + "\n",
                    e
            );
            return JetTypeInfo.create(
                    ErrorUtils.createErrorType(e.getClass().getSimpleName() + " from analyzer"),
                    context.dataFlowInfo
            );
        }
    }  

//////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public JetTypeInfo visitFunctionLiteralExpression(@NotNull JetFunctionLiteralExpression expression, ExpressionTypingContext data) {
        return expression.accept(functions, data);
    }

    @Override
    public JetTypeInfo visitNamedFunction(@NotNull JetNamedFunction function, ExpressionTypingContext data) {
        return function.accept(functions, data);
    }

//////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public JetTypeInfo visitThrowExpression(@NotNull JetThrowExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitReturnExpression(@NotNull JetReturnExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitContinueExpression(@NotNull JetContinueExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitIfExpression(@NotNull JetIfExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitTryExpression(@NotNull JetTryExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitForExpression(@NotNull JetForExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitWhileExpression(@NotNull JetWhileExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitDoWhileExpression(@NotNull JetDoWhileExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

    @Override
    public JetTypeInfo visitBreakExpression(@NotNull JetBreakExpression expression, ExpressionTypingContext data) {
        return expression.accept(controlStructures, data);
    }

//////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public JetTypeInfo visitIsExpression(@NotNull JetIsExpression expression, ExpressionTypingContext data) {
        return expression.accept(patterns, data);
    }

    @Override
    public JetTypeInfo visitWhenExpression(@NotNull JetWhenExpression expression, ExpressionTypingContext data) {
        return expression.accept(patterns, data);
    }

//////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public JetTypeInfo visitJetElement(@NotNull JetElement element, ExpressionTypingContext data) {
        return element.accept(basic, data);
    }
}
