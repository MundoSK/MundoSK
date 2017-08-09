package com.pie.tlatoani.NoteBlock;

import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.util.Pair;
import com.pie.tlatoani.Mundo;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.event.block.NotePlayEvent;

import java.util.ArrayList;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class NoteBlockMundo {
    
    public static void load() {
        ArrayList<Pair<String, Note>> notes = new ArrayList<>();
        for (int octave : new int[]{0, 1})
            for (Note.Tone tone : Note.Tone.values())
                for (int deviation : new int[]{-1, 0, 1}) {
                    if (deviation == 1 && (tone == Note.Tone.B || tone == Note.Tone.E)) continue;
                    if (deviation == -1 && (tone == Note.Tone.C || tone == Note.Tone.F)) continue;
                    Note note = Note.natural(octave, tone);
                    if (deviation == 1) note = note.sharped();
                    else if (deviation == -1) note = note.flattened();
                    String noteName = tone.name() + (deviation == 1 ? "+" : deviation == -1 ? "-" : "") + octave;
                    notes.add(new Pair<>("n" + noteName, note));
                    if (octave == 0) notes.add(new Pair<>("n" + noteName.substring(0, noteName.length() - 1), note));
                    if (!Mundo.serverHasPlugin("RandomSK")) {
                        notes.add(new Pair<>(noteName, note));
                        if (octave == 0) notes.add(new Pair<>(noteName.substring(0, noteName.length() - 1), note));
                    }
                }
        Note fSharp2 = Note.sharp(2, Note.Tone.F);
        notes.add(new Pair<>("nF+2", fSharp2));
        notes.add(new Pair<>("nG-2", fSharp2));
        if (!Mundo.serverHasPlugin("RandomSK")) {
            notes.add(new Pair<>("F+2", fSharp2));
            notes.add(new Pair<>("G-2", fSharp2));
        }
        Mundo.registerEnum(Note.class, "note", new Note[0], notes.toArray(new Pair[0]));
        Mundo.registerEnum(Instrument.class, "instrument", Instrument.values());
        Mundo.registerEffect(EffPlayNoteBlock.class, "play [[%-note% with] %-instrument% on] noteblock %block%");
        Mundo.registerEvent("Note Play", SimpleEvent.class, NotePlayEvent.class, "note play");
        Mundo.registerEventValue(NotePlayEvent.class, Note.class, NotePlayEvent::getNote);
        Mundo.registerEventValue(NotePlayEvent.class, Instrument.class, NotePlayEvent::getInstrument);
        Mundo.registerEventValue(NotePlayEvent.class, Block.class, NotePlayEvent::getBlock);
        Mundo.registerExpression(ExprNoteOfBlock.class, Note.class, ExpressionType.PROPERTY, "note of %block%", "%block%'s note");
        
    }
}
