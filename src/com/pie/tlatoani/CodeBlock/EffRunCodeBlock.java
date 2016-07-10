package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.lang.*;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.EmptyEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.TreeMap;

/**
 * Created by Tlatoani on 6/5/16.
 */
public class EffRunCodeBlock extends Effect {
    private Expression<SkriptCodeBlock> codeBlockExpression;
    private Integer mark;
    private Expression args;
    private VariableString variableString;

    @Override
    protected void execute(Event event) {
        final Event localevent;
        if (mark == 0) {
            localevent = new EmptyEvent();
        } else if (mark == 1) {
            localevent = event;
        } else if (mark == 2) {
            localevent = new EmptyEvent();
            Object[] list = args.getAll(event);
            for (int i = 1; i <= list.length; i++) {
                Variables.setVariable(i + "", list[i - 1], localevent, true);
            }
        } else {
            localevent = new EmptyEvent();
            TreeMap<String, Object> treeMap = (TreeMap) Variables.getVariable(variableString.toString(event), event, ((Variable) args).isLocal());
            treeMap.forEach((string, object) -> Variables.setVariable(string, object, localevent, true));
        }
        Mundo.debug(this, "CBE: " + codeBlockExpression);
        SkriptCodeBlock codeBlock = codeBlockExpression.getSingle(event);
        Mundo.debug(this, "getSingle: " + codeBlock);
        codeBlock.execute(localevent);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "run codeblock %codeblock% [(here|with %objects%)]";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        codeBlockExpression = (Expression<SkriptCodeBlock>) expressions[0];
        mark = parseResult.mark;
        args = expressions[1];
        if (args instanceof Variable) {
            mark = 3;
            Variable listVariable = (Variable) args;
            String origstring = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
            variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
        }
        return true;
    }

}
