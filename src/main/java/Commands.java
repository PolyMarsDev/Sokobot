import com.vdurmont.emoji.EmojiManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class Commands extends ListenerAdapter {
    HashMap<Long, Game> games = new HashMap<>();
    ArrayList<String> commandsPrefix = new ArrayList<>(Arrays.asList("play", "continue", "stop"));
    ArrayList<String> commandsNoPrefix = new ArrayList<>(Arrays.asList("w", "a", "s", "d", "up", "left", "down",
            "right", "r"));

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        Bot.removePrefix(guild.getIdLong());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        Member member = event.getMember();
        Message message = event.getMessage();
        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        String prefix = Bot.getPrefix(guild);
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
                        MessageEmbed.Footer footerObject = embed.getFooter();
                        if (footerObject != null) {
                            String footer = footerObject.getText();
                            if (footer != null) {
                                long playerId = Long.parseLong(footer.substring(10, footer.length() - 1));
                                if (games.containsKey(playerId)) {
                                    Game game = games.get(playerId);
                                    game.setGameMessage(message);
                                }
                            }
                        }
                    }
                }
            }
            return;
        }
        String[] args = message.getContentRaw().split("\\s+");
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals(prefix + "prefix")) {
                if (!hasPermissions(guild, channel)) {
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                if (member.hasPermission(Permission.ADMINISTRATOR)) {
                    if (args.length == 2 && args[1].length() == 1) {
                        String newPrefix = args[1].toLowerCase();
                        Bot.setPrefix(event.getGuild(), newPrefix);
                        channel.sendMessage("Prefix successfully changed to ``" + newPrefix + "``.").queue();
                    } else channel.sendMessage("The prefix must be one character long!").queue();
                } else
                    channel.sendMessage(user.getAsMention() + ", you do not have permission to use this " + "command" + ".").queue();
                // No need to delete prefix-set command
                // message.delete().queue();
            } else if (((commandsNoPrefix.contains(arg)) || (arg.length() > 0 && Character.toString(arg.charAt(0)).equals(prefix) && commandsPrefix.contains(arg.substring(1))))) {
                if (!hasPermissions(guild, channel)) {
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                Game game;
                if (!games.containsKey(user.getIdLong())) {
                    game = new Game(user);
                    games.put(user.getIdLong(), game);
                } else game = games.get(user.getIdLong());
                String userInput = arg;
                if (userInput.substring(0, 1).equals(prefix)) userInput = userInput.substring(1);
                if (!game.gameActive && userInput.equals("play") && args.length == 2 && EmojiManager.isEmoji(args[1])) {
                    game.setPlayerEmote(args[1]);
                }
                game.run(event.getGuild(), channel, userInput);
                if (userInput.equals("stop")) games.remove(user.getIdLong());
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) message.delete().queue();
            } else if ((arg.equals(prefix + "info")) || (message.getMentionedUsers().size() > 0 && message.getMentionedUsers().get(0).equals(event.getJDA().getSelfUser()))) {
                if (!hasPermissions(guild, channel)) {
                    sendInvalidPermissionsMessage(user, channel);
                    return;
                }
                channel.sendMessage(info(event.getGuild()).build()).queue();
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) message.delete().queue();
            }
        }
    }

    private static final Collection<Permission> requiredPermissions = Arrays.asList(Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_WRITE);

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
            if (requiredPermissionsDisplay.toString().endsWith(", "))
                requiredPermissionsDisplay = new StringBuilder(requiredPermissionsDisplay.substring(0,
                        requiredPermissionsDisplay.length() - 2));
            channel.sendMessage(user.getAsMention() + ", I don't have enough permissions to work properly.\nMake " +
                    "sure I have the following permissions: " + requiredPermissionsDisplay + "\nIf you think this is "
                    + "an error, please contact a server administrator.").queue();
        }
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
                if (!games.containsKey(user.getIdLong())) {
                    game = new Game(user);
                    games.put(user.getIdLong(), game);
                } else game = games.get(user.getIdLong());
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
                reaction.removeReaction(user).queue();
            }
        });
    }

    EmbedBuilder info(Guild guild) {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("Sokobot");
        info.setThumbnail(guild.getSelfMember().getUser().getAvatarUrl());
        info.setDescription("Sokobot is a bot that lets you play Sokoban, the classic box-pushing puzzle game.");
        info.setColor(0xdd2e53);
        info.addField("How to Play", "You are a **Sokoban** :flushed:.\nYour job is to push **boxes** :brown_square: "
                + "on top of their **destinations** :negative_squared_cross_mark:.", false);
        info.addField("Features", ":white_small_square:**Infinite levels**\nThe maps in Sokobot are randomly " +
                "generated, increasing in difficulty as you progress.\n:white_small_square:**Varied " + "controls" +
                "**\nSokobot has multiple control options to improve the player's experience, including " +
                "reactions and wasd commands!\n:white_small_square:**Simultaneous games**\nThanks to the power of " + "Java HashMaps:tm:, multiple users can use the bot at the same time without interfering with one " + "another.\n:white_small_square:**Custom prefixes**\nTo prevent Sokobot from conflicting with other " + "bots, admins can choose any single-character prefix to preface Sokobot's commands.", false);
        info.addField("Commands",
                ("``" + Bot.getPrefix(guild) + "play`` can be used to start a game if you are not " + "currently in " + "one.\n``" + Bot.getPrefix(guild) + "stop`` can be used to stop your active game at any " + "time.\n``" + Bot.getPrefix(guild) + "info`` provides some useful details about the bot and " + "rules of " + "the game.\n``" + Bot.getPrefix(guild) + "prefix [character]`` can be used to " + "change the prefix the " + "bot responds to."), false);
        info.addField("Add to your server",
                "https://top.gg/bot/713635251703906336\nSokobot is currently in " + Bot.getShardManager().getGuilds().size() + " servers.", false);
        info.addField("Source code", "https://github.com/PolyMarsDev/Sokobot", false);
        info.setFooter("created by PolyMars", "https://avatars0.githubusercontent" + ".com/u/51007356?s=460&u" +
                "=4eb8fd498421a2eee9781edfbadf654386cf06c7&v=4");
        return info;
    }

    public static void sendGameEmbed(MessageChannel channel, String level, String game, User user) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Sokobot | Level " + level);
        embed.setDescription(game);
        embed.addField("Enter direction (``up``, ``down``, ``left``, ``right``/``wasd``) or ``r`` to reset", "", false);
        embed.setFooter("Game of " + user.getAsMention(), user.getAvatarUrl());
        channel.sendMessage(embed.build()).queue();
    }

    public static void updateGameEmbed(Message message, String level, String game, User user) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Sokobot | Level " + level);
        embed.setDescription(game);
        embed.addField("Enter direction (``up``, ``down``, ``left``, ``right``/``wasd``) or ``r`` to reset", "", false);
        embed.setFooter("Game of " + user.getAsMention(), user.getAvatarUrl());
        message.editMessage(embed.build()).queue();
    }

    public static void sendWinEmbed(Guild guild, Message message, String level) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Sokobot | You win!");
        embed.setDescription("Type ``" + Bot.getPrefix(guild) + "continue`` to continue to Level " + level + " or ``" + Bot.getPrefix(guild) + "stop`` to quit ");
        embed.setFooter("You can also press any reaction to continue.");
        message.editMessage(embed.build()).queue();
    }
}
