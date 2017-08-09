package com.pie.tlatoani.Chunk;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ChunkMundo {
    
    public static void load() {
        Mundo.registerEffect(EffLoadChunk.class, "(0¦load|1¦unload) chunk %chunk%");
        Mundo.registerExpression(ExprChunkCoordinate.class, Number.class, ExpressionType.PROPERTY,
                "chunk( |-)(0¦x|1¦z)[( |-)coord] of %chunk%",
                "%chunk%'s chunk( |-)(0¦x|1¦z)[( |-)coord]");
        Mundo.registerExpression(ExprChunkWorld.class, World.class, ExpressionType.PROPERTY,
                "chunk world of %chunk%",
                "%chunk%'s chunk world");
        Mundo.registerExpression(ExprChunk.class, Chunk.class, ExpressionType.COMBINED,
                "chunk %number%, %number% [in %world%]",
                "chunks [from] %number%, %number% to %number%, %number% [in %world%]",
                "chunk at %location%",
                "chunks from %location% to %location%");
        Mundo.registerExpression(ExprChunkBlock.class, Block.class, ExpressionType.PROPERTY,
                "block %number%, %number%, %number% (of|in) %chunk%",
                "(0¦layer %-number%|1¦top|2¦bottom|3¦sea level) (0¦south|4¦north)(0¦east|8¦west) (0¦center|16¦corner) of %chunk%");
        Mundo.registerExpression(ExprChunkBlocks.class, Block.class, ExpressionType.PROPERTY,
                "[all] blocks (of|in) %chunk%",
                "blocks [from] %number%, %number%, %number% to %number%, %number%, %number% (of|in) %chunk%",
                "(0¦layer %-number%|1¦top|2¦bottom|3¦sea level) (of|in) %chunk%",
                "[[blocks] from] (0¦layer %-number%|1¦top|2¦bottom|3¦sea level) to (0¦layer %-number%|4¦top|8¦bottom|12¦sea level) (of|in) %chunk%",
                "layers [from] %number% to %number% (of|in) %chunk%"
        );
        Mundo.registerExpression(CondSlimey.class, Boolean.class, ExpressionType.PROPERTY, "%chunks% (is|are) slimey");
        Mundo.registerExpression(CondChunkLoaded.class, Boolean.class, ExpressionType.PROPERTY, "[chunk[s]] %chunks% (is|are) loaded");
    }

}
