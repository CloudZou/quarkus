package io.quarkus.qute;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * This node holds a single expression such as {@code foo.bar}.
 */
class ExpressionNode implements TemplateNode, Function<Object, CompletionStage<ResultNode>> {

    final ExpressionImpl expression;
    private final Engine engine;
    private final Origin origin;

    public ExpressionNode(ExpressionImpl expression, Engine engine, Origin origin) {
        this.expression = expression;
        this.engine = engine;
        this.origin = origin;
    }

    @Override
    public CompletionStage<ResultNode> resolve(ResolutionContext context) {
        return context.evaluate(expression).thenCompose(this);
    }

    @Override
    public CompletionStage<ResultNode> apply(Object result) {
        if (result instanceof ResultNode) {
            return CompletableFuture.completedFuture((ResultNode) result);
        } else if (result instanceof CompletionStage) {
            return ((CompletionStage<?>) result).thenCompose(this);
        } else {
            return CompletableFuture.completedFuture(new SingleResultNode(result, this));
        }
    }

    public Origin getOrigin() {
        return origin;
    }

    Engine getEngine() {
        return engine;
    }

    public Set<Expression> getExpressions() {
        return Collections.singleton(expression);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExpressionNode [expression=").append(expression).append("]");
        return builder.toString();
    }

}
