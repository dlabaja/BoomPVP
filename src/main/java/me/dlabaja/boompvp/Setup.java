package me.dlabaja.boompvp;

import me.dlabaja.boompvp.utils.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Setup {

    void Setup() {
        if(!DbExists()){
            CreateConfigDir();
            InitDB();
        }
    }

    void InitDB() {
        try (Connection conn = DriverManager.getConnection(Utils.pathToDB)) {
            if (conn != null) {
                Utils.conn = conn;
                Utils.log.info("Database connected");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void CreateConfigDir(){
        new File("plugins/boompvp").mkdirs();
    }

    boolean DbExists(){
        return new File("plugins/boompvp/db").exists();
    }
}
