package me.dlabaja.boompvp.utils;

import java.sql.*;

public class Sql {
    public static void Execute(String sql) {
        try(Connection conn = Connect()) {
            assert conn != null;
            conn.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
    }

    public static ResultSet Select(String sql) {
        try(Connection conn = Connect()) {
            assert conn != null;
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
        return null;
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

    public static Boolean PlayerExists(String name) throws SQLException {
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

    public static Object[] GetASetData(String playerName) {
        try(Connection conn = Connect()) {
            var sql = String.format("SELECT COUNT(*) FROM players WHERE name='%s'", playerName);
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
