package com.pie.tlatoani.WorldCreator;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.Pair;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldCreatorMundo {
    
    public static void load() {
        Registration.registerType(WorldCreatorData.class, "creator").serializer(new Serializer<WorldCreatorData>() {
            @Override
            public Fields serialize(WorldCreatorData creator) throws NotSerializableException {
                Fields fields = new Fields();
                creator.name.ifPresent(str -> fields.putObject("name", str));
                fields.putObject("json", creator.toJSON().toString());
                return fields;
            }

            @Override
            public void deserialize(WorldCreatorData creator, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException("WorldCreatorData does not have a nullary constructor!");
            }

            @Override
            public WorldCreatorData deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                Optional<String> name;
                try {
                    name = Optional.of((String) fields.getObject("name"));
                } catch (StreamCorruptedException e) {
                    name = Optional.empty();
                }
                try {
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse((String) fields.getObject("json"));
                    return WorldCreatorData.fromJSON(name, jsonObject).get();
                } catch (ParseException | ClassCastException | NoSuchElementException e) {
                    throw new StreamCorruptedException();
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }
        });
        Registration.registerEnum(Dimension.class, "dimension", Dimension.values(), new Pair<>("END", Dimension.THE_END));
        Registration.registerEnum(WorldType.class, "worldtype", WorldType.values(), new Pair<>("SUPERFLAT", WorldType.FLAT), new Pair<>("LARGE BIOMES", WorldType.LARGE_BIOMES), new Pair<>("VERSION 1.1", WorldType.VERSION_1_1));
        Registration.registerConverter(World.class, WorldCreatorData.class, WorldCreatorData::fromWorld);
        Registration.registerExpression(ExprNewCreator.class, WorldCreatorData.class, ExpressionType.COMBINED, "[world] creator [(with name|named) %-string%][(,| with)] [(dim[ension]|env[ironment]) %-dimension%][,] [seed %-string%][,] [[world]type %-worldtype%][,] [gen[erator] %-string%][,] [gen[erator] settings %-string%][,] [struct[ures] %-boolean%]");
        Registration.registerPropertyExpression(ExprCreatorOf.class, WorldCreatorData.class, "world", "creator");
        Registration.registerPropertyExpression(ExprNameOfCreator.class, String.class, "creator", "worldname");
        Registration.registerPropertyExpression(ExprDimensionOfCreator.class, Dimension.class, "creator", "dim", "dimension", "env", "environment");
        Registration.registerPropertyExpression(ExprTypeOfCreator.class, WorldType.class, "creator", "worldtype");
        Registration.registerPropertyExpression(ExprSeedOfCreator.class, String.class, "creator", "seed");
        Registration.registerPropertyExpression(ExprGeneratorOfCreator.class, String.class, "creator", "gen", "generator");
        Registration.registerPropertyExpression(ExprGeneratorSettingsOfCreator.class, String.class, "creator", "gen set", "generator set", "gen settings", "generator settings");
        Registration.registerPropertyExpression(ExprStructuresOfCreator.class, Boolean.class, "creator", "struct", "structures", "structure settings");
    }
}
