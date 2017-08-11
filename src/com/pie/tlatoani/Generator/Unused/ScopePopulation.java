package com.pie.tlatoani.Generator.Unused;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Trigger;
import com.pie.tlatoani.Util.CustomScope;

/**
 * Created by Tlatoani on 8/23/16.
 */
public class ScopePopulation extends CustomScope {
    @Override
    public String getString() {
        return "population";
    }

    @Override
    public boolean init() {
        if (!ScriptLoader.isCurrentEvent(SkriptGeneratorEvent.class)) {
            Skript.error("The 'population' scope can only be used in a custom world generator!");
            return false;
        }
        return true;
    }

    @Override
    public void setScope() {
        if (!(scopeParent instanceof Trigger) || !(((Trigger) scopeParent).getEvent() instanceof EvtChunkGenerator)) {
            Skript.error("The 'population' scope should be immediately under the 'custom generator' event scope, not below any further scopes!");
        } else {
            if (scope.getNext() != null) {
                Skript.error("The 'population' scope should be the last item in a custom generator!");
            }
            ((EvtChunkGenerator) ((Trigger) scopeParent).getEvent()).getGenerator().population = first;
        }
    }
}
