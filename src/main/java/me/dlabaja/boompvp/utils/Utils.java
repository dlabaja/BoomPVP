package me.dlabaja.boompvp.utils;

import java.sql.Connection;
import java.util.logging.Logger;

public class Utils {
    public static Logger log;
    public static String pathToDB = "jdbc:sqlite:plugins/boompvp/db.db";
    public static Connection conn;
}
