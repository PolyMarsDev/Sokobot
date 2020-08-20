package me.polymarsdev.sokobot.database;

import java.io.File;
import java.sql.*;

public class Database {

    public enum DBType {MySQL, SQLite}

    /**
     * SQLite Data
     * Set this data if you use DBType#SQLite
     *
     * field filePath - This can either be a relative or absolute path.
     * ex: sokobot.db
     * or: C:/sqlite/db/sokobot.db
     */
    private final String filePath = "sokobot.db";

    /**
     * MySQL Data
     * Set this data if you use DBType#MySQL
     */
    private final String mysql_hostname = "127.0.0.1";
    private final int mysql_port = 3306;
    private final String mysql_database = "sokobot";
    private final String mysql_username = "sokobot";
    private final String mysql_password = "$€cUR€_P4sSw0R!)";

    private Connection con = null;

    public Database(DBType dbType) {
        try {
            if (dbType == DBType.MySQL) {
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + mysql_hostname + ":" + mysql_port + "/" + mysql_database
                                + "?autoReconnect=true", mysql_username, mysql_password);
                System.out.println("[INFO] Successfully initialized database connection.");
            } else if (dbType == DBType.SQLite) {
                File sqliteFile = new File(filePath);
                if (!sqliteFile.exists()) {
                    System.out.println("[INFO] SQLite file \"" + filePath + "\" not found, creating file...");
                    boolean create = sqliteFile.createNewFile();
                    if (!create) System.out.println("[ERROR] Could not create SQLite file at " + filePath);
                }
                con = DriverManager.getConnection("jdbc:sqlite:" + filePath);
                System.out.println("[INFO] Successfully initialized database connection.");
            }
        } catch (Exception ex) {
            System.out.println("[ERROR] Error at creating database connection: " + ex.getMessage());
        }
    }

    public void disconnect() {
        try {
            con.clearWarnings();
            con.close();
            con = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getCon() {
        return con;
    }

    public ResultSet query(String sql, Object... preparedParameters) {
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            int id = 1;
            for (Object preparedParameter : preparedParameters) {
                ps.setObject(id, preparedParameter);
                id++;
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet query(String sql) {
        try {
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void update(String sql, Object... preparedParameters) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int id = 1;
            for (Object preparedParameter : preparedParameters) {
                ps.setObject(id, preparedParameter);
                id++;
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(String sql) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isConnected() {
        return con != null;
    }
}