package me.dlabaja.boompvp;

import jdk.jshell.execution.Util;
import me.dlabaja.boompvp.utils.Sql;
import me.dlabaja.boompvp.utils.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Setup {

    void Setup() {
        if (!DbExists()){
            CreateDB();
        }
        Utils.log.info("Setup completed");
    }

    void CreateDB() {
        Utils.log.info("First time using? Creating a database...");
        CreateConfigDir();
        Sql.Execute("CREATE TABLE players (" +
                "name varchar(255)," +
                "kills int," +
                "deaths int," +
                "killstreak int" +
                ")");
        Utils.log.info("Database created!");

    }

    void CreateConfigDir() {
        new File("plugins/boompvp").mkdirs();
    }

    boolean DbExists() {
        return new File(Utils.pathToDB).exists();
    }
}
