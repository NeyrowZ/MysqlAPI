package net.neyrowz.mysql.api.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class Utils {

    public static FileConfiguration getCredentials(Plugin plugin) {
        File credentials = new File(plugin.getDataFolder() + "/credentials.yml");
        if (!credentials.exists()) plugin.saveResource("credentials.yml", true);
        return YamlConfiguration.loadConfiguration(credentials);
    }

    public static String classToMysqlType(Object type) {
        if (int.class.equals(type)) {
            return "INT";
        } else if (double.class.equals(type)) {
            return "DOUBLE";
        } else if (long.class.equals(type)) {
            return "LONG";
        } else if (String.class.equals(type)) {
            return "VARCHAR(100)";
        } else if (UUID.class.equals(type)) {
            return "VARCHAR(36)";
        } else if (Date.class.equals(type)) {
            return "DATE";
        } else {
            return "TEXT";
        }
    }
}