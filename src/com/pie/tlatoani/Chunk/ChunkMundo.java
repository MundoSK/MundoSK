package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Core.Registration.Registration;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ChunkMundo {
    
    public static void load() {
        Registration.registerEffect(EffLoadChunk.class, "(0¦load|1¦unload) chunk %chunk%")
                .document("Load Chunk", "1.8", "Loads or unloads the specified chunk.");
        Registration.registerExpression(ExprChunkCoordinate.class, Number.class, ExpressionType.PROPERTY,
                "chunk( |-)(0¦x|1¦z)[( |-)coord] of %chunk%",
                "%chunk%'s chunk( |-)(0¦x|1¦z)[( |-)coord]")
                .document("Coordinate of Chunk", "1.8", "The x or z-coordinate of the specified chunk");
        Registration.registerExpression(ExprChunkWorld.class, World.class, ExpressionType.PROPERTY,
                "chunk world of %chunk%",
                "%chunk%'s chunk world")
                .document("World of Chunk", "1.8", "The world in which the specified chunk is located");
        Registration.registerExpression(ExprChunk.class, Chunk.class, ExpressionType.COMBINED,
                "chunk %number%, %number% [in %world%]",
                "chunks [from] %number%, %number% to %number%, %number% [in %world%]",
                "chunk at %location%",
                "chunks from %location% to %location%")
                .document("Chunk at Coordinates", "1.8", "The first is the chunk at the specified coordinates."
                        , "The second is the chunks between the specified coordinates."
                        , "The third is the chunk that contains the specified location."
                        , "The fourth is the chunks between the specified locations.");
        Registration.registerExpression(ExprChunkBlock.class, Block.class, ExpressionType.PROPERTY,
                "block %number%, %number%, %number% (of|in) %chunk%",
                "(0¦layer %-number%|1¦top|2¦bottom|3¦sea level) (0¦south|4¦north)(0¦east|8¦west) (0¦center|16¦corner) of %chunk%")
                .document("Block in Chunk", "1.8", "The first is the block at the specified coordinates within the specified chunk"
                        + " The second is a center or corner block of a layer, one of the three defaults or a specified number, of the specified chunk.");
        Registration.registerExpression(ExprChunkBlocks.class, Block.class, ExpressionType.PROPERTY,
                "[all] blocks (of|in) %chunk%",
                "blocks [from] %number%, %number%, %number% to %number%, %number%, %number% (of|in) %chunk%",
                "(0¦layer %-number%|1¦top|2¦bottom|3¦sea level) (of|in) %chunk%",
                "[[blocks] from] (0¦layer %-number%|1¦top|2¦bottom|3¦sea level) to (0¦layer %-number%|4¦top|8¦bottom|12¦sea level) (of|in) %chunk%",
                "layers [from] %number% to %number% (of|in) %chunk%")
                .document("Blocks in Chunk", "1.8", "The first is all of the blocks in the specified chunk."
                        , "The second is the blocks between two coordinates of the specified chunk."
                        , "The third is a layer, one of the three defaults or a specified number, of the specified chunk."
                        , "The fourth is the layers between two specified numbers of the specified chunk.");
        Registration.registerExpressionCondition(CondSlimey.class, ExpressionType.PROPERTY, "%chunks% (0¦is|0¦are|1¦isn't|1¦is not|1¦aren't|1¦are not) slimey")
                .document("Chunk is Slimey", "1.8", "Checks whether the specified chunk is slimey.");
        Registration.registerExpressionCondition(CondChunkLoaded.class, ExpressionType.PROPERTY, "[chunk[s]] %chunks% (0¦is|0¦are|1¦isn't|1¦is not|1¦aren't|1¦are not) loaded")
                .document("Chunk is Loaded", "1.8", "Checks whether the specified chunk is loaded.");
    }

}
