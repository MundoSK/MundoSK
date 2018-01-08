package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/14/16.
 */
public class ExprFunctionCodeBlock extends SimpleExpression<CodeBlock> {
    Expression<String> stringExpression;

    @Override
    protected CodeBlock[] get(Event event) {
        Function function = Functions.getFunction(stringExpression.getSingle(event));
        return new CodeBlock[]{new FunctionCodeBlock(function)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends CodeBlock> getReturnType() {
        return FunctionCodeBlock.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "codeblock of function " + stringExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        stringExpression = (Expression<String>) expressions[0];
        return true;
    }
}
