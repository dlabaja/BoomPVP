package me.dlabaja.boompvp.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Utils {
    public static Logger log;
    public static String dbString = "jdbc:sqlite:plugins/boompvp/db.db";
    public static String pathToDir = "plugins/boompvp";
    public static String pathToDB = "";
    public static String pathToConfig = "plugins/boompvp/config.properties";
    public static String pathToJson = "plugins/boompvp/spawn.json";

    public static int Parse(String string) {
        return Integer.parseInt(string);
    }

    public static class FormatMsg {

        public static String formatMsg(String msg, Player player) {
            return ChatColor.translateAlternateColorCodes('&', msg.replace("%player", player.getName()));
        }
        public static String formatMsg(String msg, Player player, Player killer) {
            var string = msg.replace("%player", player.getName());
            return ChatColor.translateAlternateColorCodes('&', string.replace("%killer", killer.getName()));
        }
        public static String formatMsg(String msg, Player killer, int killstreak) {
            var string = msg.replace("%killer", killer.getName());
            return ChatColor.translateAlternateColorCodes('&', string.replace("%killstreak", String.valueOf(killstreak)));
        }
    }
}
