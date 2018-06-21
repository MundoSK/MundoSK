package com.pie.tlatoani.Miscellaneous.NoteBlock;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class EffPlayNoteBlock extends Effect {
    private Expression<Note> noteExpression;
    private Expression<Instrument> instrumentExpression;
    private Expression<Block> blockExpression;

    @Override
    protected void execute(Event event) {
        Block block = blockExpression.getSingle(event);
        NoteBlock noteBlock = (NoteBlock) block.getState();
        if (instrumentExpression == null) {
            noteBlock.play();
        } else {
            Logging.debug(this, "Instrument: " + instrumentExpression.getSingle(event));
            Logging.debug(this, "Note: " + (noteExpression == null ? noteBlock.getNote() : noteExpression.getSingle(event)));
            noteBlock.play(instrumentExpression.getSingle(event), (noteExpression == null ? noteBlock.getNote() : noteExpression.getSingle(event)));
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "play [[%note% with] %instrument% on] noteblock %block%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        noteExpression = (Expression<Note>) expressions[0];
        instrumentExpression = (Expression<Instrument>) expressions[1];
        blockExpression = (Expression<Block>) expressions[2];
        return true;
    }
}
