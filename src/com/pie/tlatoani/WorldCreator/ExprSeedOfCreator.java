package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprSeedOfCreator extends EvolvingPropertyExpression<WorldCreatorData, String> {
    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, String s) {
        return worldCreatorData.setSeed(Optional.of(Long.parseLong(s)));
    }

    @Override
    public WorldCreatorData reset(WorldCreatorData worldCreatorData) {
        return worldCreatorData.setSeed(Optional.empty());
    }

    @Override
    public String convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.seed.map(Object::toString).orElse(null);
    }
}
