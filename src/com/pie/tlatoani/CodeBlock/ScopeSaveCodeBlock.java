package com.pie.tlatoani.CodeBlock;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

import java.util.TreeMap;

/**
 * Created by Tlatoani on 6/5/16.
 */
public class ScopeSaveCodeBlock extends CustomScope {
    private Variable variable;
    private VariableString variableString;
    private Boolean variableIsLocal;
    private Expression<?> constant = null;
    private VariableString constantVariableString;
    private int mark;
    private Expression<String> argumentNames;


    @Override
    public boolean init(Expression<?>[] exprs, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        if (exprs[0] instanceof Variable) {
            variable = (Variable) exprs[0];
            String origstring = variable.isLocal() ? variable.toString().substring(2, variable.toString().length() - 1) : variable.toString().substring(1, variable.toString().length() - 1);
            variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
            variableIsLocal = variable.isLocal();
            Mundo.debug(this, "exprs[0]: " + variable);
            mark = arg3.mark;
            if (mark > 1) {
                constant = exprs[mark - 1];
                if (mark == 3 && constant instanceof Variable) {
                    mark = 4;
                    Variable listVariable = (Variable) constant;
                    String origstring1 = listVariable.isLocal() ? listVariable.toString().substring(2, listVariable.toString().length() - 1) : listVariable.toString().substring(1, listVariable.toString().length() - 1);
                    constantVariableString = VariableString.newInstance(origstring1, StringMode.VARIABLE_NAME);
                }
            }
            argumentNames = (Expression<String>) exprs[3];
            return true;
        }
        Skript.error(exprs[0].toString() + " is not a variable!");
        return false;
    }

    @Override
    public String getString() {
        return "codeblock %variable%";
    }

    @Override
    public void go(Event e) {
        ScopeCodeBlock scopeCodeBlock = new ScopeCodeBlock(first, mark != 0, argumentNames == null ? null : argumentNames.getArray(e));
        switch (mark) {
            case 2: scopeCodeBlock.setConstantSingle(constant.getSingle(e));
                break;
            case 3: scopeCodeBlock.setConstantArray(constant.getArray(e));
                break;
            case 4: scopeCodeBlock.setConstantListVariable((TreeMap) Variables.getVariable(constantVariableString.toString(e), e, ((Variable) constant).isLocal()));
        }
        Variables.setVariable(variableString.toString(e), scopeCodeBlock, e, variableIsLocal);
    }
}
