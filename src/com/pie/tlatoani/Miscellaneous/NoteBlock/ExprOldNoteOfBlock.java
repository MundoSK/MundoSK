package com.pie.tlatoani.Miscellaneous.NoteBlock;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class ExprOldNoteOfBlock extends SimpleExpression<Note> {
    private Expression<Block> blockExpression;

    @Override
    protected Note[] get(Event event) {
        Logging.debug(this, "");
        return new Note[]{((NoteBlock) blockExpression.getSingle(event).getState()).getNote()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Note> getReturnType() {
        return Note.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "note of %block%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        blockExpression = (Expression<Block>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        ((NoteBlock) blockExpression.getSingle(event).getState()).setNote((Note) delta[0]);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Note.class);
        return null;
    }
}
