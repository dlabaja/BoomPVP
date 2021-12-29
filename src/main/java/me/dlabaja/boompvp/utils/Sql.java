package me.dlabaja.boompvp.utils;

import java.sql.*;
import java.util.function.Supplier;

public class Sql {
    public void InsertToSql(String sql) {
        try {
            var conn = Utils.conn;
            conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
    }

    /*public String GetFromSql(String sql) {
        try {
            var conn = Utils.conn;
            var st = conn.createStatement().executeQuery(sql);
            while (st.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
                float price = rs.getFloat("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");
                System.out.println(coffeeName + ", " + supplierID + ", " + price +
                        ", " + sales + ", " + total);
            }
        } catch (SQLException e) {
            Utils.log.severe(e.getMessage());
        }
    }*/
}
