package com.pie.tlatoani.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 6/5/16.
 */
public class ScopeSaveCodeBlock extends CustomScope {
    private VariableString variableString;
    private Boolean variableIsLocal;
    private SkriptCodeBlock skriptCodeBlock;

    @Override
    public boolean init(Expression<?>[] exprs, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        if (exprs[0] instanceof Variable) {
            Variable variable = (Variable) exprs[0];
            String origstring = variable.isLocal() ? variable.toString().substring(2, variable.toString().length() - 1) : variable.toString().substring(1, variable.toString().length() - 1);
            variableString = VariableString.newInstance(origstring, StringMode.VARIABLE_NAME);
            variableIsLocal = variable.isLocal();
            Mundo.debug(this, "exprs[0]: " + variable);
            return true;
        }
        Skript.error(exprs[0].toString() + " is not a variable!");
        return false;
    }

    @Override
    public void afterSetNext() {
        skriptCodeBlock = new SkriptCodeBlock(first);
        Mundo.debug(this, "SkCB: " + skriptCodeBlock);
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "save codeblock in %variable%";
    }

    @Override
    public void go(Event e) {
        Variables.setVariable(variableString.toString(e), skriptCodeBlock, e, variableIsLocal);
    }
}
