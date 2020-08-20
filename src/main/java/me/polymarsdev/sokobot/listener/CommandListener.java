package me.polymarsdev.sokobot.listener;

import me.polymarsdev.sokobot.Bot;
import me.polymarsdev.sokobot.Game;
import me.polymarsdev.sokobot.commands.GameInputCommand;
import me.polymarsdev.sokobot.commands.InfoCommand;
import me.polymarsdev.sokobot.commands.PrefixCommand;
import me.polymarsdev.sokobot.entity.Command;
import me.polymarsdev.sokobot.event.CommandEvent;
import me.polymarsdev.sokobot.util.GameUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class CommandListener extends ListenerAdapter {
    private static final ArrayList<String> commandsNoPrefix = new ArrayList<>(
            Arrays.asList("w", "a", "s", "d", "up", "left", "down", "right", "r", "mr"));
    private static final HashMap<String, Command> commands = new HashMap<>();

    public CommandListener() {
        List<Command> botCommands = new ArrayList<>(Arrays.asList(new InfoCommand(), new PrefixCommand()));
        botCommands.addAll(Arrays.asList(new GameInputCommand("play"), new GameInputCommand("continue"),
                                         new GameInputCommand("stop")));
        for (String cnp : commandsNoPrefix) botCommands.add(new GameInputCommand(cnp));
        for (Command command : botCommands) commands.put(command.getName().toLowerCase(), command);
        System.out.println("[INFO] Loaded " + commands.size() + " commands");
    }


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        Message message = event.getMessage();
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        if (user.getId().equals(event.getJDA().getSelfUser().getId())) {
            List<MessageEmbed> embeds = message.getEmbeds();
            if (embeds.size() > 0) {
                MessageEmbed embed = embeds.get(0);
                if (embed.getTitle() != null && embed.getTitle().length() > 0) {
                    if (embed.getTitle().startsWith("Sokobot | Level ")) {
                        message.addReaction("U+2B05").queue();
                        message.addReaction("U+27A1").queue();
                        message.addReaction("U+2B06").queue();
                        message.addReaction("U+2B07").queue();
                        message.addReaction("U+1F504").queue();
                        List<MessageEmbed.Field> fields = embed.getFields();
                        for (MessageEmbed.Field field : fields) {
                            if (field.getName() != null && field.getName().equals("Player")) {
                                if (field.getValue() != null) {
                                    long playerId = Long
                                            .parseLong(field.getValue().substring(2, field.getValue().length() - 1));
                                    if (GameUtil.hasGame(playerId)) {
                                        Game game = GameUtil.getGame(playerId);
                                        game.setGameMessage(message);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
        String msgRaw = message.getContentRaw();
        String[] args = msgRaw.split("\\s+");
        if (args.length > 0) {
            boolean isMention = msgRaw.equals("<@" + event.getJDA().getSelfUser().getId() + ">") || msgRaw
                    .equals("<@!" + event.getJDA().getSelfUser().getId() + ">");
            String prefix = Bot.getPrefix(guild);
            String arg = args[0].toLowerCase();
            boolean isCommand;
            if (isMention) isCommand = true;
            else {
                if (arg.startsWith(prefix)) {
                    if (commandsNoPrefix.contains(arg)) {
                        isCommand = true;
                    } else {
                        String commandName = arg.substring(prefix.length()).toLowerCase();
                        isCommand = commands.containsKey(commandName);
                        if (isCommand) arg = commandName;
                    }
                } else {
                    isCommand = commandsNoPrefix.contains(arg);
                }
            }
            if (isCommand) {
                if (!hasPermissions(guild, channel)) {
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                Command command = commands.get(arg);
                if (isMention) command = commands.get("info");
                if (command == null) return;
                command.execute(new CommandEvent(event, Arrays.copyOfRange(msgRaw.split("\\s+"), 1, args.length)));
            }
        }
    }

    private static final Collection<Permission> requiredPermissions = Arrays
            .asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE,
                    Permission.MESSAGE_WRITE);

    private boolean hasPermissions(Guild guild, TextChannel channel) {
        Member self = guild.getSelfMember();
        if (self.hasPermission(Permission.ADMINISTRATOR)) return true;
        return self.hasPermission(channel, requiredPermissions);
    }

    private void sendInvalidPermissionsMessage(User user, TextChannel channel) {
        if (channel.canTalk()) {
            StringBuilder requiredPermissionsDisplay = new StringBuilder();
            for (Permission requiredPermission : requiredPermissions) {
                requiredPermissionsDisplay.append("`").append(requiredPermission.getName()).append("`, ");
            }
            if (requiredPermissionsDisplay.toString().endsWith(", ")) requiredPermissionsDisplay = new StringBuilder(
                    requiredPermissionsDisplay.substring(0, requiredPermissionsDisplay.length() - 2));
            channel.sendMessage(user.getAsMention() + ", I don't have enough permissions to work properly.\nMake "
                                        + "sure I have the following permissions: " + requiredPermissionsDisplay
                                        + "\nIf you think this is "
                                        + "an error, please contact a server administrator.").queue();
        }
    }
}