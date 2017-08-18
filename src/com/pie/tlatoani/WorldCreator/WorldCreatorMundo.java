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

/**
 * Created by Tlatoani on 8/8/17.
 */
public class WorldCreatorMundo {
    
    public static void load() {
        Registration.registerType(WorldCreatorData.class, "creator").parser(new Registration.SimpleParser<WorldCreatorData>() {
            @Override
            public WorldCreatorData parse(String s, ParseContext parseContext) {
                return null;
            }

            @Override
            public String toString(WorldCreatorData creator, int flags) {
                return creator.name + ":" + creator.toJSON();
            }
        }).serializer(new Serializer<WorldCreatorData>() {
            @Override
            public Fields serialize(WorldCreatorData creator) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("name", creator.name);
                fields.putObject("json", creator.toJSON().toString());
                return fields;
            }

            @Override
            public void deserialize(WorldCreatorData creator, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException("WorldCreatorData does not have a nullary constructor!");
            }

            @Override
            public WorldCreatorData deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                try {
                    String name = (String) fields.getObject("name");
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
        Registration.registerExpression(ExprNewCreator.class, WorldCreatorData.class, ExpressionType.COMBINED, "[world] creator (with name|named) %string%[(,| with)] [(dim[ension]|env[ironment]) %-dimension%][,] [seed %-string%][,] [[world]type %-worldtype%][,] [gen[erator] %-string%][,] [gen[erator] settings %-string%][,] [struct[ures] %-boolean%]");
        Registration.registerPropertyExpression(ExprCreatorOf.class, WorldCreatorData.class, "world", "creator");
        Registration.registerPropertyExpression(ExprNameOfCreator.class, String.class, "creator", "worldname");
        Registration.registerPropertyExpression(ExprDimensionOfCreator.class, Dimension.class, "creator", "dim", "dimension", "env", "environment");
        Registration.registerPropertyExpression(ExprTypeOfCreator.class, WorldType.class, "creator", "worldtype");
        Registration.registerPropertyExpression(ExprSeedOfCreator.class, String.class, "creator", "seed");
        Registration.registerPropertyExpression(ExprGeneratorOfCreator.class, String.class, "creator", "gen", "generator");
        Registration.registerPropertyExpression(ExprGeneratorSettingsOfCreator.class, String.class, "creator", "gen set", "generator set", "gen settings", "generator settings");
        Registration.registerPropertyExpression(ExprStructuresOfCreator.class, Boolean.class, "creator", "struct", "structures", "structure settings");

        /*Registration.registerType(WorldCreator.class, "creator").parser(new Registration.SimpleParser<WorldCreator>() {
            @Override
            public WorldCreator parse(String s, ParseContext parseContext) {
                return null;
            }

            @Override
            public String toString(WorldCreator creator, int flags) {
                JSONObject jsonObject = WorldLoader.getCreatorJSON(creator);
                jsonObject.put("worldname", creator.name());
                return jsonObject.toString();
            }

        });
        if (!MundoUtil.serverHasPlugin("RandomSK")) {
            Registration.registerEnum(World.Environment.class, "environment", World.Environment.values(), new Pair<String, World.Environment>("END", World.Environment.THE_END));
        }
        Registration.registerEnum(WorldType.class, "worldtype", WorldType.values(), new Pair<String, WorldType>("SUPERFLAT", WorldType.FLAT), new Pair<String, WorldType>("LARGE BIOMES", WorldType.LARGE_BIOMES), new Pair<String, WorldType>("VERSION 1.1", WorldType.VERSION_1_1));
        Registration.registerExpression(OldExprCreatorNamed.class,WorldCreator.class, ExpressionType.PROPERTY,"creator (with name|named) %string%");
        Registration.registerExpression(OldExprCreatorWith.class,WorldCreator.class,ExpressionType.PROPERTY,"%creator%[ modified],[ name %-string%][,][ (environment|env[ironment]) %-environment%][,][ seed %-string%][,][ type %-worldtype%][,][ gen[erator] %-string%][,][ gen[erator] settings %-string%][,][ struct[ures] %-boolean%]");
        Registration.registerExpression(OldExprCreatorOf.class,WorldCreator.class,ExpressionType.PROPERTY,"creator of %world%");
        Registration.registerExpression(ExprOldNameOfCreator.class,String.class,ExpressionType.PROPERTY,"worldname of %creator%");
        Registration.registerExpression(OldExprEnvOfCreator.class,World.Environment.class,ExpressionType.PROPERTY,"env[ironment] of %creator%");
        Registration.registerExpression(OldExprSeedOfCreator.class,String.class,ExpressionType.PROPERTY,"seed of %creator%");
        Registration.registerExpression(OldExprGenOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] of %creator%");
        Registration.registerExpression(OldExprGenSettingsOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] set[tings] of %creator%");
        Registration.registerExpression(OldExprTypeOfCreator.class,WorldType.class,ExpressionType.PROPERTY,"worldtype of %creator%");
        Registration.registerExpression(OldExprStructOfCreator.class,Boolean.class,ExpressionType.PROPERTY,"struct[ure(s| settings)] of %creator%");*/
    }
}
