package me.dlabaja.boompvp.utils;

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

    public static void SaveData(Player player, BoomPVPPrvky _boomPVPPrvky){
        Sql.Execute(String.format("UPDATE players SET kills = %o, deaths = %o, killstreak = %o WHERE name = '%s';", _boomPVPPrvky.killy.get(player), _boomPVPPrvky.smrti.get(player), _boomPVPPrvky.killstreak.get(player), player.getName()));
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
            var sql = String.format("SELECT * FROM players WHERE name='%s'", playerName);
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
