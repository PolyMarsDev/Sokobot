package me.polymarsdev.sokobot;

import me.polymarsdev.sokobot.database.Database;
import me.polymarsdev.sokobot.listener.CommandListener;
import me.polymarsdev.sokobot.listener.GameListener;
import me.polymarsdev.sokobot.util.GameUtil;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class Bot {
    static HashMap<Long, String> prefixes = new HashMap<>();

    /**
     * You can enable the database here.
     * Set the DB Type to MySQL or SQLite, which you want to use.
     * -
     * You can configure login data in the Database class.
     */
    private static final boolean enableDatabase = false;
    private static final Database.DBType dbType = Database.DBType.SQLite;

    private static ShardManager shardManager;
    private static Database database = null;

    public static void main(String[] args) throws LoginException {
        String token = null;
        try {
            File tokenFile = Paths.get("token.txt").toFile();
            if (!tokenFile.exists()) {
                System.out.println("[ERROR] Could not find token.txt file");
                System.out.print("Please paste in your bot token: ");
                Scanner s = new Scanner(System.in);
                token = s.nextLine();
                System.out.println();
                System.out.println("[INFO] Creating token.txt - please wait");
                if (!tokenFile.createNewFile()) {
                    System.out.println(
                            "[ERROR] Could not create token.txt - please create this file and paste in your token"
                                    + ".");
                    s.close();
                    return;
                }
                Files.write(tokenFile.toPath(), token.getBytes());
                s.close();
            }
            token = new String(Files.readAllBytes(tokenFile.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (token == null) return;
        if (enableDatabase) database = new Database(dbType);
        if (database != null) {
            if (!database.isConnected()) {
                database = null;
                System.out.println("[ERROR] Database connection failed. Continuing without database.");
            } else {
                database.update(
                        "CREATE TABLE IF NOT EXISTS guildprefix (guildId VARCHAR(18) NOT NULL, prefix VARCHAR(8) NOT "
                                + "NULL);");
            }
        }
        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Sokobot for info!"));
        builder.addEventListeners(new GameListener(), new CommandListener());
        shardManager = builder.build();
        GameUtil.runGameTimer();
        Thread consoleThread = new Thread(() -> {
            Scanner s = new Scanner(System.in);
            while (s.hasNextLine()) {
                processCommand(s.nextLine());
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.setName("Console Thread");
        consoleThread.start();
    }

    private static void processCommand(String cmd) {
        if (cmd.equalsIgnoreCase("help")) {
            System.out.println("Commands:\nstop - Shuts down the bot and exits the program");
            return;
        }
        if (cmd.equalsIgnoreCase("stop")) {
            System.out.println("Shutting down...");
            shardManager.shutdown();
            if (database != null) {
                System.out.println("Disconnecting database...");
                database.disconnect();
            }
            System.out.println("Bye!");
            System.exit(0);
            return;
        }
        System.out.println("Unknown command. Please use \"help\" for a list of commands.");
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static void removePrefix(long guildId) {
        prefixes.remove(guildId);
        if (database != null) {
            database.update("DELETE FROM guildprefix WHERE guildId=?;", String.valueOf(guildId));
        }
    }

    public static void setPrefix(Guild guild, String prefix) {
        prefixes.put(guild.getIdLong(), prefix);
        if (database != null) {
            database.update("DELETE FROM guildprefix WHERE guildId=?;", guild.getId());
            database.update("INSERT INTO guildprefix VALUES (?, ?);", guild.getId(), prefix);
        }
    }

    public static String getPrefix(Guild guild) {
        if (prefixes.containsKey(guild.getIdLong())) return prefixes.get(guild.getIdLong());
        if (database != null) {
            try (ResultSet rs = database.query("SELECT prefix FROM guildprefix WHERE guildId=?;", guild.getId())) {
                if (rs.next()) {
                    String prefix = rs.getString("prefix");
                    prefixes.put(guild.getIdLong(), prefix);
                    return prefix;
                }
                prefixes.put(guild.getIdLong(), "!");
                return "!";
            } catch (SQLException ex) {
                System.out.println("[ERROR] Error at retrieving guild prefix of guild id " + guild.getId() + ": " + ex
                        .getMessage());
            }
        }
        return "!";
    }
}