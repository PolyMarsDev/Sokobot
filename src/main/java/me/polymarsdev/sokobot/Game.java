package me.polymarsdev.sokobot;

import me.polymarsdev.sokobot.objects.Grid;
import me.polymarsdev.sokobot.util.GameUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
            width = 9;
            height = 6;
            for (int i = 1; i < level; i++) updateWidthHeight();
            grid = new Grid(width, height, level, playerEmote);
            gameActive = true;
            lastAction = System.currentTimeMillis();
            GameUtil.sendGameEmbed(channel, String.valueOf(level), grid.toString(), user);
        }
    }

    // This method used to something earlier. (I actually just forgot what I used it for)
    // It did not work like it was supposed to, so it was changed to this basic line.
    private void queue(RestAction<Message> restAction, Consumer<? super Message> success) {
        restAction.queue(success);
    }

    public void stop() {
        gameActive = false;
        TextChannel textChannel = Bot.getShardManager().getTextChannelById(channelID);
        if (textChannel != null) {
            textChannel.retrieveMessageById(gameMessageID).queue(gameMessage -> gameMessage.delete().queue());
        }
    }

    public void run(Guild guild, TextChannel channel, String userInput) {
        if (userInput.equals("stop") && gameActive) {
            stop();
            channel.sendMessage("Thanks for playing, " + user.getAsMention() + "!")
                   .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        }
        if (userInput.equals("play") && !gameActive) {
            newGame(channel);
        } else if (gameActive) {
            lastAction = System.currentTimeMillis();
            boolean won = grid.hasWon();
            if (!won) {
                boolean moved = false;
                switch (userInput) {
                    case "up":
                    case "w":
                        moved = grid.getPlayer().moveUp();
                        break;
                    case "down":
                    case "s":
                        moved = grid.getPlayer().moveDown();
                        break;
                    case "left":
                    case "a":
                        moved = grid.getPlayer().moveLeft();
                        break;
                    case "right":
                    case "d":
                        moved = grid.getPlayer().moveRight();
                        break;
                    case "mr":
                        grid.resetMap();
                        moved = true;
                        break;
                    case "r":
                        grid.reset();
                        moved = true;
                        break;
                }
                grid.updateGrid();
                won = grid.hasWon();
                if (!won && moved) {
                    TextChannel textChannel = Bot.getShardManager().getTextChannelById(channelID);
                    if (textChannel != null) {
                        queue(textChannel.retrieveMessageById(gameMessageID), gameMessage -> GameUtil
                                .updateGameEmbed(gameMessage, String.valueOf(level), grid.toString(), user));
                    }
                }
            }
            if (won) {
                level += 1;
                updateWidthHeight();
                TextChannel textChannel = Bot.getShardManager().getTextChannelById(channelID);
                if (textChannel != null) {
                    queue(
                            textChannel.retrieveMessageById(gameMessageID),
                            gameMessage -> GameUtil.sendWinEmbed(guild, gameMessage, String.valueOf(level)));
                }
                grid = new Grid(width, height, level, playerEmote);
            }
        }
    }

    private void updateWidthHeight() {
        if (width < 13) {
            width += 2;
        }
        if (height < 8) {
            height += 1;
        }
    }
}
