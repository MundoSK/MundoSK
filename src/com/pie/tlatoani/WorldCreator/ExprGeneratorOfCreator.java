package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Core.Skript.EvolvingPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprGeneratorOfCreator extends EvolvingPropertyExpression<WorldCreatorData, String> {
    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, String s) {
        return worldCreatorData.setGeneratorID(s);
    }

    @Override
    public WorldCreatorData reset(WorldCreatorData worldCreatorData) {
        return worldCreatorData.setGeneratorID(null);
    }

    @Override
    public String convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.getGeneratorID().orElse(null);
    }
}
