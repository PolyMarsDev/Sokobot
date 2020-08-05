import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class Bot {
    static HashMap<Long, String> prefixes = new HashMap<>();

    private static ShardManager shardManager;

    public static void main(String[] args) throws LoginException {
        String token = null;
        try {
            File tokenFile = Paths.get("token.txt").toFile();
            if (!tokenFile.exists()) {
                System.out.println("[ERROR] Could not find token.txt file");
                System.out.println("[ERROR] Please create a file called \"token.txt\" in the same folder as the jar " + "file and paste in your bot token.");
                return;
            }
            token = new String(Files.readAllBytes(tokenFile.toPath()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (token == null) return;
        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Sokobot for info!"));
        builder.addEventListeners(new Commands());
        shardManager = builder.build();
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
            System.out.println("Bye!");
            System.exit(0);
            return;
        }
        System.out.println("Unknown command. Please use \"help\" for a list of commands.");
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    static void setPrefix(Guild guild, String prefix) {
        prefixes.put(guild.getIdLong(), prefix);
    }

    static String getPrefix(Guild guild) {
        return prefixes.getOrDefault(guild.getIdLong(), "!");
    }
}