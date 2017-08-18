package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprStructuresOfCreator extends EvolvingPropertyExpression<WorldCreatorData, Boolean> {
    @Override
    public WorldCreatorData evolve(WorldCreatorData worldCreatorData, Boolean bool) {
        return worldCreatorData.setStructures(bool);
    }

    @Override
    public Boolean convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.structures;
    }
}
