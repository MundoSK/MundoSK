package com.pie.tlatoani.Generator;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Trigger;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.CustomScope;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class ScopeGeneration extends CustomScope {
    @Override
    public String getString() {
        return "generation";
    }

    @Override
    public boolean init() {
        if (!ScriptLoader.isCurrentEvent(SkriptGeneratorEvent.class)) {
            Skript.error("The 'generation' scope can only be used in a custom world generator!");
            return false;
        }
        return true;
    }

    @Override
    public void setScope() {
        if (!(scopeParent instanceof Trigger)) {
            Skript.error("The 'generation' scope should be immediately under the event scope, not below any further scopes!");
        } else if (scope.getNext() != null) {
            if (!(scopeNext instanceof Conditional)) {
                Skript.error("The 'generation' scope should either be right before the 'population' scope or be the last item in a custom generator!");
            } else {
                try {
                    Condition condition = (Condition) CustomScope.condition.get(scopeNext);
                    if (!(condition instanceof ScopePopulation)) {
                        Skript.error("The 'generation' scope should either be right before the 'population' scope or be the last item in a custom generator!");
                    }
                } catch (IllegalAccessException e) {
                    Mundo.reportException(this, e);
                }
            }
        }
        last.setNext(null);
    }
}
