package me.polymarsdev.sokobot;

import me.polymarsdev.sokobot.objects.Grid;
import me.polymarsdev.sokobot.util.GameUtil;
import net.dv8tion.jda.api.entities.*;

import java.util.concurrent.TimeUnit;

public class Game {
    long gameMessageID;
    long channelID;
    User user;
    String playerEmote = ":flushed:";
    public boolean gameActive = false;
    public int level = 1;
    int width = 9;
    int height = 6;
    public long lastAction;
    Grid grid = new Grid(width, height, level, playerEmote);

    public Game(User user) {
        this.user = user;
    }

    public void setPlayerEmote(String emote) {
        playerEmote = emote;
    }

    public void setGameMessage(Message gameMessage) {
        // To avoid an Unknown Message error, we will store the IDs and retrieve the Channel object when needed.
        gameMessageID = gameMessage.getIdLong();
        channelID = gameMessage.getChannel().getIdLong();
    }

    public void newGame(MessageChannel channel) {
        if (!gameActive) {
            level = 1;
            width = 9;
            height = 6;
            grid = new Grid(width, height, level, playerEmote);
            gameActive = true;
            lastAction = System.currentTimeMillis();
            GameUtil.sendGameEmbed(channel, String.valueOf(level), grid.toString(), user);
        }
    }

    public void stop() {
        gameActive = false;
        TextChannel textChannel = Bot.getShardManager().getTextChannelById(channelID);
        if (textChannel != null) {
            textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> gameMessage.delete().queue());
        }
    }

    public void run(Guild guild, TextChannel channel, String userInput) {
        lastAction = System.currentTimeMillis();
        if (userInput.equals("stop") && gameActive) {
            stop();
            channel.sendMessage("Thanks for playing, " + user.getAsMention() + "!")
                    .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        }
        if (userInput.equals("play") && !gameActive) {
            newGame(channel);
        } else if (gameActive) {
            if (!grid.hasWon()) {
                String direction = userInput;
                switch (direction) {
                    case "up":
                    case "w":
                        grid.getPlayer().moveUp();
                        break;
                    case "down":
                    case "s":
                        grid.getPlayer().moveDown();
                        break;
                    case "left":
                    case "a":
                        grid.getPlayer().moveLeft();
                        break;
                    case "right":
                    case "d":
                        grid.getPlayer().moveRight();
                        break;
                    case "mr":
                        grid.resetMap();
                        break;
                    case "r":
                        grid.reset();
                        break;
                }
                if (!grid.hasWon()) {
                    TextChannel textChannel = Bot.getShardManager().getTextChannelById(channelID);
                    if (textChannel != null) {
                        textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> GameUtil
                                .updateGameEmbed(gameMessage, String.valueOf(level), grid.toString(), user));
                    }
                }
            }
            if (grid.hasWon()) {
                level += 1;
                if (width < 13) {
                    width += 2;
                }
                if (height < 8) {
                    height += 1;
                }
                TextChannel textChannel = Bot.getShardManager().getTextChannelById(channelID);
                if (textChannel != null) {
                    textChannel.retrieveMessageById(gameMessageID)
                            .queue(gameMessage -> GameUtil.sendWinEmbed(guild, gameMessage, String.valueOf(level)));
                }
                grid = new Grid(width, height, level, playerEmote);
            }
        }
    }
}
