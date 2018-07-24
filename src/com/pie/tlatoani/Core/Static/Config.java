package com.pie.tlatoani.Core.Static;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.MineSkin.PlayerSkinRetrieval;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Tlatoani on 8/21/17.
 */
public final class Config {
    private static final List<Option> options = new ArrayList<>();

    public static final Option<List<String>> DEBUG_PACKAGES = new Option<>("debug", Arrays.asList(new String[0]), FileConfiguration::getStringList);
    public static final Option<Boolean> IMPLEMENT_PACKET_STUFF = new Option<>("enable_custom_skin_and_tablist", true, FileConfiguration::getBoolean);
    public static final Option<Integer> TABLIST_SPAWN_REMOVE_TAB_DELAY = new Option<>("tablist_remove_tab_delay_spawn", 5, FileConfiguration::getInt);
    public static final Option<Integer> TABLIST_RESPAWN_REMOVE_TAB_DELAY = new Option<>("tablist_remove_tab_delay_respawn", 5, FileConfiguration::getInt);
    public static final Option<Integer> TABLIST_ADD_TO_DEFAULT_GROUP_DELAY = new Option<>("tablist_add_to_default_group_delay", 5, FileConfiguration::getInt);
    public static final Option<Boolean> ENABLE_OFFLINE_PLAYER_SKIN_CACHE = new Option<>("enable_offline_player_skin_cache", true, FileConfiguration::getBoolean);
    public static final Option<Integer> OFFLINE_PLAYER_SKIN_CACHE_MAX_SIZE = new Option<>("offline_player_skin_cache_max_size", 1000, FileConfiguration::getInt);
    public static final Option<Integer> OFFLINE_PLAYER_SKIN_CACHE_EXPIRE_TIME_MINUTES = new Option<>("offline_player_skin_cache_expire_time_minutes", 30, FileConfiguration::getInt);
    public static final Option<Boolean> DISABLE_SIZE_SYNTAX = new Option<>("border_disable_size_syntax", false, FileConfiguration::getBoolean);

    public static void reload() {
        Mundo.get().reloadConfig();
        FileConfiguration config = Mundo.get().getConfig();
        options.forEach(option -> option.addDefault(config));
        config.options().copyDefaults(true);
        options.forEach(option -> option.loadValue(config));
        Mundo.get().saveConfig();
        PlayerSkinRetrieval.reloadSkinCache();
    }

    public static void displayConfig(CommandSender sender) {
        for (Option option : options) {
            sender.sendMessage(MainCommand.formatMundoSKInfo(option.path, option.getCurrentValue().toString()));
        }
    }

    public static class Option<T> {
        public final String path;
        private final T defaultValue;
        private final BiFunction<FileConfiguration, String, T> function;

        private T currentValue = null;

        private Option(String path, T defaultValue, BiFunction<FileConfiguration, String, T> function) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.function = function;
            options.add(this);
        }

        public T getCurrentValue() {
            return currentValue;
        }

        private void addDefault(FileConfiguration config) {
            config.addDefault(path, defaultValue);
        }

        private void loadValue(FileConfiguration config) {
            currentValue = function.apply(config, path);
        }
    }
}