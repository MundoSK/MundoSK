package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.*;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.EmptyEvent;
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
        Event localevent = null;
        if (mark == 0 || mark == 4) {
            localevent = new EmptyEvent();
        } else if (mark == 1 || mark == 5) {
            localevent = event;
        } else if (mark == 2 || mark == 6) {
            localevent = new EmptyEvent();
            Object[] list = args.getAll(event);
            for (int i = 1; i <= list.length; i++) {
                Variables.setVariable(i + "", list[i - 1], localevent, true);
            }
        } else if (mark == 3 || mark == 7){
            final EmptyEvent tempevent = new EmptyEvent();
            TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(variableString.toString(event), event, ((Variable) args).isLocal());
            treeMap.forEach(tempevent::setLocalVariable);
            localevent = tempevent;
        }
        return localevent;
    }

    @Override
    protected void execute(Event event) {
        if (mark < 4) {
            for (CodeBlock codeBlock : codeBlockExpression.getArray(event)) {
                codeBlock.execute(getLocalEvent(event, mark));
            }
        } else {
            Event localevent = getLocalEvent(event, mark);
            for (CodeBlock codeBlock : codeBlockExpression.getArray(event)) {
                codeBlock.execute(localevent);
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
        if (args instanceof Variable) {
            mark++;
            Variable listVariable = (Variable) args;
            String origstring = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
            variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
        }
        return true;
    }

}
