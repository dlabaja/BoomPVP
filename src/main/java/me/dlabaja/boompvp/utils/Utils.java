package me.dlabaja.boompvp.utils;

import java.util.logging.Logger;

public class Utils {
    public static Logger log;
    public static String dbString = "jdbc:sqlite:plugins/boompvp/db.db";
    public static String pathToDB = "";
    public static String pathToConfig = "plugins/boompvp/config.properties";
    public static String pathToJson = "plugins/boompvp/spawn.json";

    public static int Parse(String string) {
        return Integer.parseInt(string);
    }
}
