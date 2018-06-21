package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Core.Skript.EvolvingPropertyExpression;
import org.bukkit.WorldType;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprTypeOfCreator extends EvolvingPropertyExpression<WorldCreatorData, WorldType> {
    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, WorldType worldType) {
        return worldCreatorData.setType(worldType);
    }

    @Override
    public WorldCreatorData reset(WorldCreatorData worldCreatorData) {
        return worldCreatorData.setType(null);
    }

    @Override
    public WorldType convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.type;
    }
}
