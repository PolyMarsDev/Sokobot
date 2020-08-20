package me.polymarsdev.sokobot.listener;

import me.polymarsdev.sokobot.Bot;
import me.polymarsdev.sokobot.Game;
import me.polymarsdev.sokobot.util.GameUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GameListener extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        Bot.removePrefix(guild.getIdLong());
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        User user = event.getUser();
        if (user.isBot()) {
            return;
        }
        Guild guild = event.getGuild();
        MessageReaction reaction = event.getReaction();
        TextChannel channel = event.getChannel();
        channel.retrieveMessageById(event.getMessageId()).queue(message -> {
            if (message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                Game game;
                if (!GameUtil.hasGame(user.getIdLong())) {
                    game = new Game(user);
                    GameUtil.setGame(user.getIdLong(), game);
                } else game = GameUtil.getGame(user.getIdLong());
                boolean reactionCommand = true;
                String userInput = "";
                switch (event.getReactionEmote().toString()) {
                    case "RE:U+2b05":
                        userInput = "left";
                        break;
                    case "RE:U+27a1":
                        userInput = "right";
                        break;
                    case "RE:U+2b06":
                        userInput = "up";
                        break;
                    case "RE:U+2b07":
                        userInput = "down";
                        break;
                    case "RE:U+1f504":
                        userInput = "r";
                        break;
                    default:
                        reactionCommand = false;
                        break;
                }
                if (reactionCommand) game.run(guild, channel, userInput);
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE))
                    reaction.removeReaction(user).queue();
            }
        });
    }

}
