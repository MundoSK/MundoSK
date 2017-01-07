package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Tlatoani on 1/3/17.
 */
public class ExprTreeAtLoc extends SimpleExpression<Block> {
    private Expression<Location> locationExpression;

    public static Collection<Block> treeAt(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
            return Collections.emptyList();
        }
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(block);
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block[] adjacentBlocks = {
                    currentBlock.getRelative(1, 0, 0),
                    currentBlock.getRelative(-1, 0, 0),
                    currentBlock.getRelative(0, 1, 0),
                    currentBlock.getRelative(0, -1, 0),
                    currentBlock.getRelative(0, 0, 1),
                    currentBlock.getRelative(0, 0, -1)
            };
            for (Block adjacentBlock : adjacentBlocks) {
                if ((adjacentBlock.getType() == Material.LOG || adjacentBlock.getType() == Material.LOG_2) && !blocks.contains(adjacentBlock)) {
                    blocks.add(adjacentBlock);
                } else if ((adjacentBlock.getType() == Material.LEAVES || adjacentBlock.getType() == Material.LEAVES_2) && !blocks.contains(adjacentBlock)) {
                    for (Block log : blocks) {
                        //if ((log.getType() == Material.LOG || log.getType() == Material.LOG_2) &&
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    protected Block[] get(Event event) {
        return treeAt(locationExpression.getSingle(event)).toArray(new Block[0]);
    }

    @Override
    public Iterator<Block> iterator(Event event) {
        return treeAt(locationExpression.getSingle(event)).iterator();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "tree at " + locationExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        locationExpression = (Expression<Location>) expressions[0];
        return true;
    }
}
