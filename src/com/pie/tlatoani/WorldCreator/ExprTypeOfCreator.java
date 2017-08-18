package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;
import org.bukkit.WorldType;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprTypeOfCreator extends EvolvingPropertyExpression<WorldCreatorData, WorldType> {
    @Override
    public WorldCreatorData evolve(WorldCreatorData worldCreatorData, WorldType worldType) {
        return worldCreatorData.setType(worldType);
    }

    @Override
    public WorldType convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.type;
    }
}
