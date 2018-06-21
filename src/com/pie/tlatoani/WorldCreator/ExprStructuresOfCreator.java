package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Core.Skript.EvolvingPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprStructuresOfCreator extends EvolvingPropertyExpression<WorldCreatorData, Boolean> {
    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, Boolean bool) {
        return worldCreatorData.setStructures(bool);
    }

    @Override
    public WorldCreatorData reset(WorldCreatorData worldCreatorData) {
        return worldCreatorData.setStructures(null);
    }

    @Override
    public Boolean convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.structures;
    }
}
