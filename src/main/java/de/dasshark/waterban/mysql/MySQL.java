package de.dasshark.waterban.mysql;

import de.dasshark.waterban.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MySQL {
    public static String username;
    public static String password;
    public static String database;
    public static String host;
    public static String port;
    public static Connection con;

    public MySQL() {
    }

    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                System.out.println(Main.getInstance().prefix + "MySQL-Verbindung aufgebaut!");
            } catch (SQLException var1) {
                var1.printStackTrace();
            }
        }

    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                con.close();
                System.out.println(Main.getInstance().prefix + "MySQL-Verbindung geschlossen!");
            } catch (SQLException var1) {
                var1.printStackTrace();
            }
        }

    }

    public static boolean isConnected() {
        return con != null;
    }

    public static void createTable() {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS waterBan (NAME VARCHAR(100), UUID VARCHAR(100), Banned BOOLEAN,  BanReason VARCHAR(100), Muted BOOLEAN, Until VARCHAR(100), Permanent BOOLEAN, MuteHistory INTEGER, BanHistory INTEGER, KickHistory INTEGER)");
            } catch (SQLException var1) {
                var1.printStackTrace();
            }
        }

    }

    public static void update(String qry) {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate(qry);
            } catch (SQLException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static ResultSet getResultSet(String qry) {
        if (isConnected()) {
            try {
                return con.createStatement().executeQuery(qry);
            } catch (SQLException var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }

    public static void set(String set, String setresult, String where, String whereresult) {
        update("UPDATE waterBan SET " + set + "='" + setresult + "' WHERE " + where + "='" + whereresult + "'");
    }

    public static Object get(String select, String where, String whereresult) {
        ResultSet rs = getResultSet("SELECT `" + select + "` FROM waterBan WHERE " + where + "='" + whereresult + "'");

        try {
            if (rs.next()) {
                Object v = rs.getObject(select);
                return v;
            } else {
                return "ERROR";
            }
        } catch (SQLException var5) {
            return "ERROR";
        }
    }

    public static boolean existsPlayer(ProxiedPlayer p) {
        ResultSet rs = getResultSet("SELECT * FROM waterBan WHERE UUID='" + p.getUniqueId().toString() + "'");

        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException var3) {
        }

        return false;
    }
}
