import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Bot {
    static HashMap<Long, String> prefixes = new HashMap<>();

    public static void main(String[] args) throws LoginException, IOException {
        String token = new String(Files.readAllBytes(Paths.get("token.txt")));
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setToken(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Sokobot for info!"));
        builder.addEventListeners(new Commands());
        builder.build();
    }

    static void setPrefix(Guild guild, String prefix)
    {
        prefixes.put(guild.getIdLong(), prefix);
    }

    static String getPrefix(Guild guild)
    {
        if (!prefixes.containsKey(guild.getIdLong()))
        {
            return "!";
        }
        return prefixes.get(guild.getIdLong());
    }
}