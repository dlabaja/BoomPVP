package me.dlabaja.boompvp.utils;

import org.bukkit.Location;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    public static Properties properties = new Properties();
    public static int version;
    public static int world_border_size;
    public static int world_border_min_size;
    public static int time;
    public static int max_height;
    public static int spawn_height;
    public static int min_height;
    public static int world_border_damage;
    public static double height_damage;
    public static boolean fall_damage;
    public static boolean day_night_cycle;
    public static boolean throw_items;
    public static String death_suicide_message;
    public static String death_knocked_message;
    public static String death_projectile_message;
    public static String death_default_message;
    public static String killstreak_message;
    public static String join_message;
    public static String quit_message;

    public static void Setup() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream(Utils.pathToConfig));
            var propt = properties;

            Utils.pathToDB = (String) propt.get("db_location");
            world_border_size = Utils.Parse((String) propt.get("world_border_size"));
            world_border_min_size = Utils.Parse((String) propt.get("world_border_min_size"));
            world_border_damage = Utils.Parse((String) propt.get("world_border_damage"));
            min_height = Utils.Parse((String) propt.get("min_height"));
            spawn_height = Utils.Parse((String) propt.get("spawn_height"));
            max_height = Utils.Parse((String) propt.get("max_height"));
            height_damage = Double.parseDouble((String) propt.get("height_damage"));
            time = Utils.Parse((String) propt.get("time"));
            fall_damage = Boolean.parseBoolean((String) propt.get("fall_damage"));
            day_night_cycle = Boolean.parseBoolean((String) propt.get("day_night_cycle"));
            throw_items = Boolean.parseBoolean((String) propt.get("throw_items"));
            death_suicide_message = (String) propt.get("death_suicide_message");
            death_knocked_message = (String) propt.get("death_knocked_message");
            death_projectile_message = (String) propt.get("death_projectile_message");
            death_default_message = (String) propt.get("death_default_message");
            killstreak_message = (String) propt.get("killstreak_message");
            join_message = (String) propt.get("join_message");
            quit_message = (String) propt.get("quit_message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
