package me.polymarsdev.sokobot.commands;

import com.vdurmont.emoji.EmojiManager;
import me.polymarsdev.sokobot.Bot;
import me.polymarsdev.sokobot.Game;
import me.polymarsdev.sokobot.entity.Command;
import me.polymarsdev.sokobot.event.CommandEvent;
import me.polymarsdev.sokobot.util.GameUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class GameInputCommand extends Command {

    public GameInputCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandEvent event) {
        User user = event.getAuthor();
        String[] args = event.getArgs();
        String prefix = Bot.getPrefix(event.getGuild());
        Game game;
        if (!GameUtil.hasGame(user.getIdLong())) {
            game = new Game(user);
            GameUtil.setGame(user.getIdLong(), game);
        } else game = GameUtil.getGame(user.getIdLong());
        //
        String userInput = this.getName().toLowerCase();
        if (userInput.equals("play")) {
            if (!game.gameActive) {
                if (args.length > 0 && EmojiManager.isEmoji(args[0])) game.setPlayerEmote(args[0]);
            } else {
                event.reply(user.getAsMention() + ", you already have an active game.\nUse `" + prefix
                                    + "stop` to stop your current game first.");
            }
        }
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        game.run(event.getGuild(), channel, userInput);
        if (userInput.equals("stop")) GameUtil.removeGame(user.getIdLong());
        if (game.gameActive && guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
            event.getMessage().delete().queue();
    }
}
