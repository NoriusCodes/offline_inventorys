package me.offlineinvs.data;

import me.offlineinvs.OfflineInvs;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private static String host;
    private static String port;
    private static String database;
    private static String username;
    private static String password;
    private static Connection con;

    public static void connect() {
        if (isConnected()) return;

        host = OfflineInvs.getInstance().getConfig().getString("MySQL.host");
        port = OfflineInvs.getInstance().getConfig().getString("MySQL.port");
        database = OfflineInvs.getInstance().getConfig().getString("MySQL.database");
        username = OfflineInvs.getInstance().getConfig().getString("MySQL.username");
        password = OfflineInvs.getInstance().getConfig().getString("MySQL.password");

        if(host.isEmpty() || port.isEmpty() || database.isEmpty() || username.isEmpty() || password.isEmpty()) {
            OfflineInvs.getInstance().getLogger().info("[MySQL] Verbindungsinformationen konnten nicht abgerufen werden!");
            Bukkit.getPluginManager().disablePlugin(OfflineInvs.getInstance());
            return;
        }

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            OfflineInvs.getInstance().getLogger().info("[MySQL] Erfolgreich Verbunden!");

            PreparedStatement inventorys = con.prepareStatement("CREATE TABLE IF NOT EXISTS player_inventorys(uuid VARCHAR(100) PRIMARY KEY, items TEXT)");
            inventorys.executeUpdate();
            inventorys.close();

            PreparedStatement enderchests = con.prepareStatement("CREATE TABLE IF NOT EXISTS player_enderchests(uuid VARCHAR(100) PRIMARY KEY, items TEXT)");
            enderchests.executeUpdate();
            enderchests.close();
        } catch (SQLException e) {
            Bukkit.getPluginManager().disablePlugin(OfflineInvs.getInstance());
            OfflineInvs.getInstance().getLogger().info("[MySQL] Verbindung nicht möglich");
            throw new RuntimeException(e);
        }
    }

    public static void disconnect() {
        if (!isConnected()) return;
        try {
            con.close();
            OfflineInvs.getInstance().getLogger().info("[MySQL] Verbindung getrennt!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isConnected() {
        return (con != null);
    }

    public static Connection getConnection() {
        return con;
    }
}
