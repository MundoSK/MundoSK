package com.pie.tlatoani.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

/**
 * Created by Tlatoani on 6/5/16.
 */
public class ScopeSaveCodeBlock extends CustomScope {
    private Variable variable;
    private SkriptCodeBlock skriptCodeBlock;

    @Override
    public boolean init(Expression<?>[] exprs, int arg1, Kleenean arg2, SkriptParser.ParseResult arg3) {
        if (exprs[0] instanceof Variable) {
            variable = (Variable) exprs[0];
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
        variable.change(e, new Object[]{skriptCodeBlock}, Changer.ChangeMode.SET);
    }
}
