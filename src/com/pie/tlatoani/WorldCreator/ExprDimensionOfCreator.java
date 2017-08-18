package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;
import org.bukkit.World;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprDimensionOfCreator extends EvolvingPropertyExpression<WorldCreatorData, Dimension> {
    @Override
    public Dimension convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.dimension;
    }

    @Override
    public WorldCreatorData evolve(WorldCreatorData worldCreatorData, Dimension dimension) {
        return worldCreatorData.setDimension(dimension);
    }
}
