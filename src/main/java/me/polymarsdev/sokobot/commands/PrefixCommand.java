package me.polymarsdev.sokobot.commands;

import me.polymarsdev.sokobot.Bot;
import me.polymarsdev.sokobot.entity.Command;
import me.polymarsdev.sokobot.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void execute(CommandEvent event) {
        User user = event.getAuthor();
        Member member = event.getMember();
        String[] args = event.getArgs();
        if (args.length > 0) {
            if (!member.hasPermission(Permission.ADMINISTRATOR)) {
                event.reply(user.getAsMention() + ", you do not have permission to use this command.");
                return;
            }
            String newPrefix = args[0].toLowerCase();
            if (newPrefix.length() > 1) {
                event.reply(user.getAsMention() + ", the prefix must be one character long!");
                return;
            }
            Bot.setPrefix(event.getGuild(), newPrefix);
            event.reply("Prefix successfully changed to ``" + newPrefix + "``.");
        }
    }
}
