import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Bot {
    static HashMap<Guild, String> prefixes = new HashMap<Guild, String>();

    public static void main(String[] args) throws LoginException, IOException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = new String(Files.readAllBytes(Paths.get("token.txt")));
        builder.setToken(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Sokobot for info!"));
        builder.addEventListeners(new Commands());
        builder.build();
    }

    static void setPrefix(Guild guild, String prefix)
    {
        prefixes.put(guild, prefix);
    }

    static String getPrefix(Guild guild)
    {
        if (!prefixes.containsKey(guild))
        {
            return "!";
        }
        return prefixes.get(guild);
    }
}