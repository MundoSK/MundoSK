package com.pie.tlatoani.WorldCreator;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Registration.Registration;
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
        Registration.registerType(WorldCreatorData.class, "creator")
                .document("WorldCreator", "1.8", "A WorldCreator is a way to store data about the various properties of a world for creating it, "
                        + "and can be used in the Create World effect and in Automatic Creator syntax elements to load worlds automatically. "
                        + "See the Property of Creator expressions for more information.")
                .serializer(new Serializer<WorldCreatorData>() {
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
        Registration.registerEnum(Dimension.class, "dimension", Dimension.values())
                .pair("END", Dimension.THE_END)
                .document("Dimension", "1.8", "A Minecraft world's dimension, also known as an environment.");
        Registration.registerEnum(WorldType.class, "worldtype", WorldType.values())
                .pair("SUPERFLAT", WorldType.FLAT)
                .pair("LARGE BIOMES", WorldType.LARGE_BIOMES)
                .pair("VERSION 1.1", WorldType.VERSION_1_1)
                .document("WorldType", "1.8", "A Minecraft world's world type.");
        Registration.registerConverter(World.class, WorldCreatorData.class, WorldCreatorData::fromWorld);
        Registration.registerExpression(ExprNewCreator.class, WorldCreatorData.class, ExpressionType.COMBINED,
                "[world] creator [(with name|named) %-string%][(,| with)] [(dim[ension]|env[ironment]) %-dimension%][,] [seed %-string%][,] [[world]type %-worldtype%][,] [gen[erator] %-string%][,] [gen[erator] settings %-string%][,] [struct[ures] %-boolean%]")
                .document("New Creator", "1.8", "An expression for a creator with the specified name (or no name) and the specified properties. "
                        + "See the WorldCreator type and the Property of WorldCreator expressions for more info.");
        Registration.registerPropertyExpression(ExprCreatorOf.class, WorldCreatorData.class, "world", "creator")
                .document("Creator of World", "1.8", "An expression for a creator with the properties of the specified world.");
        Registration.registerPropertyExpression(ExprNameOfCreator.class, String.class, "creator", "worldname")
                .document("Name of Creator", "1.8", "An expression for the worldname of a creator. "
                        + "Not all creators have a set worldname, in which case when creating a world using one you must specify a name.");
        Registration.registerPropertyExpression(ExprDimensionOfCreator.class, Dimension.class, "creator", "dim", "dimension", "env", "environment")
                .document("Dimension of Creator", "1.8", "An expression for the dimension, or environment, of a creator. This is 'normal' by default.");
        Registration.registerPropertyExpression(ExprTypeOfCreator.class, WorldType.class, "creator", "worldtype")
                .document("WorldType of Creator", "1.8", "An expression for the worldtype of a creator. This is 'default' by default.");
        Registration.registerPropertyExpression(ExprSeedOfCreator.class, String.class, "creator", "seed")
                .document("Seed of Creator", "1.8", "An expression for the seed of a creator. "
                        + "This isn't necessarily set, in which case a random seed will be used when creating a world using the creator.");
        Registration.registerPropertyExpression(ExprGeneratorOfCreator.class, String.class, "creator", "gen", "generator")
                .document("Generator of Creator", "1.8", "An expression for the custom generator of a creator. "
                        + "This will not be set if a creator is not specified to use a custom generator.");
        Registration.registerPropertyExpression(ExprGeneratorSettingsOfCreator.class, String.class, "creator", "gen set", "generator set", "gen settings", "generator settings")
                .document("Generator Settings of Creator", "1.8", "An expression for the generator settings of a creator. "
                        + "This can be a superflat world code or customized world code (the worldtype must be set as superflat and customized respectively in these cases). "
                        + "In addition, a custom generator might also use generator settings.");
        Registration.registerPropertyExpression(ExprStructuresOfCreator.class, Boolean.class, "creator", "struct", "structures", "structure settings")
                .document("Structure Settings of Creator", "1.8", "An condition/expression for whether a creator is set to generate structures.");
    }
}
