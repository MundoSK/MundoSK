package com.pie.tlatoani.Generator.Unused;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Conditional;
import ch.njol.skript.lang.Trigger;
import com.pie.tlatoani.Util.CustomScope;
import com.pie.tlatoani.Util.Logging;

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
        if (!(scopeParent instanceof Trigger) || !(((Trigger) scopeParent).getEvent() instanceof EvtChunkGenerator)) {
            Skript.error("The 'generation' scope should be immediately under the 'custom generator' event scope, not below any further scopes!");
        } else {
            if (scope.getNext() != null) {
                if (!(scope.getNext() instanceof Conditional)) {
                    Skript.error("The 'generation' scope should either be right before the 'population' scope or be the last item in a custom generator!");
                } else {
                    try {
                        Condition condition = (Condition) CustomScope.condition.get(scope.getNext());
                        if (!(condition instanceof ScopePopulation)) {
                            Skript.error("The 'generation' scope should either be right before the 'population' scope or be the last item in a custom generator!");
                        }
                    } catch (IllegalAccessException e) {
                        Logging.reportException(this, e);
                    }
                }
            }
            ((EvtChunkGenerator) ((Trigger) scopeParent).getEvent()).getGenerator().generation = first;
        }
        last.setNext(null);
    }
}
