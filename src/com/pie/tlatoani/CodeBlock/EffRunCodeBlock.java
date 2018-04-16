package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.Skript.BaseEvent;
import org.bukkit.event.Event;

import java.util.TreeMap;

/**
 * Created by Tlatoani on 6/5/16.
 */
public class EffRunCodeBlock extends Effect {
    private Expression<CodeBlock> codeBlockExpression;
    private Integer mark;
    private Expression args;
    private VariableString variableString;
    private boolean isVariable = false;

    private Event getLocalEvent(Event event, int mark) {
        if (mark == 0 || mark == 4) {
            return new BaseEvent();
        } else if (mark == 5) {
            return event;
        } else if (mark == 3 || mark == 7){
            final BaseEvent tempevent = new BaseEvent();
            TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(variableString.toString(event), event, ((Variable) args).isLocal());
            treeMap.forEach(tempevent::setLocalVariable);
            return tempevent;
        }
        return null;
    }

    @Override
    protected void execute(Event event) {
        if (mark == 2) {
            for (CodeBlock codeBlock : codeBlockExpression.getArray(event)) {
                codeBlock.execute(args.getArray(event));
            }
        } else if (mark < 4) {
            for (CodeBlock codeBlock : codeBlockExpression.getArray(event)) {
                codeBlock.execute(getLocalEvent(event, mark), false);
            }
        } else {
            Event localevent = getLocalEvent(event, mark);
            for (CodeBlock codeBlock : codeBlockExpression.getArray(event)) {
                codeBlock.execute(localevent, true);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "run codeblock %codeblock% [(here|with %objects%)]";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        codeBlockExpression = (Expression<CodeBlock>) expressions[0];
        mark = parseResult.mark;
        args = expressions[1];
        if (mark == 3 || mark == 7) {
            if (args instanceof Variable) {
                Variable listVariable = (Variable) args;
                String origstring = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
                variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
            } else {
                Skript.error("The 'run codeblock %codeblock% with variables' effect must be used with a list variable!");
            }
        }

        return true;
    }

}
