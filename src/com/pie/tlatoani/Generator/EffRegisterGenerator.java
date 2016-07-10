package com.pie.tlatoani.Generator;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.CodeBlock.SkriptCodeBlock;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/5/16.
 */
public class EffRegisterGenerator extends Effect {
    private Expression<String> generatorID;
    private Expression<SkriptCodeBlock> generateChunks;
    private Expression<SkriptCodeBlock> getSpawn;

    @Override
    protected void execute(Event event) {
        SkriptChunkGenerator generator = ChunkGeneratorManager.getSkriptGenerator(generatorID.getSingle(event));
        generator.setGenerateChunkData(generateChunks.getSingle(event));
        if (getSpawn == null) {
            generator.setGetFixedSpawnLocation(getSpawn.getSingle(event));
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "register custom generator with id " + generatorID + " to generate chunks through " + generateChunks + (getSpawn == null ? "" : " and get fixed spawn through " + getSpawn);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        generatorID = (Expression<String>) expressions[0];
        generateChunks = (Expression<SkriptCodeBlock>) expressions[1];
        getSpawn = (Expression<SkriptCodeBlock>) expressions[2];
        return true;
    }
}
