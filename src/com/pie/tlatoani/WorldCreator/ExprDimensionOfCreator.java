package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Core.Skript.EvolvingPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprDimensionOfCreator extends EvolvingPropertyExpression<WorldCreatorData, Dimension> {
    @Override
    public Dimension convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.dimension;
    }

    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, Dimension dimension) {
        return worldCreatorData.setDimension(dimension);
    }

    @Override
    public WorldCreatorData reset(WorldCreatorData worldCreatorData) {
        return worldCreatorData.setDimension(null);
    }
}
