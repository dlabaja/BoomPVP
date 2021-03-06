package me.dlabaja.boompvp.utils;

import me.dlabaja.boompvp.BoomPVP;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Sql {
    public static void Execute(String sql) {
        try(Connection conn = Connect()) {
            assert conn != null;
            conn.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
    }

    public static Connection Connect(){
        try {
            return DriverManager.getConnection(Utils.dbString);
        } catch (SQLException e) {
            return null;
        }
    }

    public static String AddPlayer(String name){
        Utils.log.info("Creating stats for player " + name);
        return String.format("INSERT INTO players(name, kills, deaths, killstreak) VALUES('%s', 0, 0, 0)", name);
    }

    public static Boolean PlayerExists(String name) {
        try(Connection conn = Connect()) {
            assert conn != null;
            var rs = conn.createStatement().executeQuery(String.format("SELECT COUNT(*) FROM players WHERE name='%s'", name));
            assert rs != null;
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
        return false;
    }

    public static Object[] GetASetData(Player player) {
        try(Connection conn = Connect()) {
            var sql = String.format("SELECT * FROM players WHERE name='%s'", player.getName());
            assert conn != null;
            var rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("name");
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                int killstreak = rs.getInt("killstreak");
                return new Object[]{name, kills, deaths, killstreak};
            }
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
        return new Object[0];
    }
}
