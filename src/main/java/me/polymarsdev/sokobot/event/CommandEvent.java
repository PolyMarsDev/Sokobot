package me.polymarsdev.sokobot.event;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public class CommandEvent {
    private static final int MAX_MESSAGES = 2;

    private final GuildMessageReceivedEvent event;
    private final String[] args;

    public CommandEvent(GuildMessageReceivedEvent event, String[] args) {
        this.event = event;
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public void reply(String message) {
        sendMessage(event.getChannel(), message);
    }

    public void reply(MessageEmbed embed) {
        event.getChannel().sendMessage(embed).queue();
    }

    private void sendMessage(MessageChannel chan, String message) {
        ArrayList<String> messages = splitMessage(message);
        for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
            chan.sendMessage(messages.get(i)).queue();
        }
    }

    private static ArrayList<String> splitMessage(String stringtoSend) {
        ArrayList<String> msgs = new ArrayList<>();
        if (stringtoSend != null) {
            stringtoSend = stringtoSend.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
            while (stringtoSend.length() > 2000) {
                int leeway = 2000 - (stringtoSend.length() % 2000);
                int index = stringtoSend.lastIndexOf("\n", 2000);
                if (index < leeway) index = stringtoSend.lastIndexOf(" ", 2000);
                if (index < leeway) index = 2000;
                String temp = stringtoSend.substring(0, index).trim();
                if (!temp.equals("")) msgs.add(temp);
                stringtoSend = stringtoSend.substring(index).trim();
            }
            if (!stringtoSend.equals("")) msgs.add(stringtoSend);
        }
        return msgs;
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public Member getMember() {
        return event.getMember();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public TextChannel getTextChannel() {
        return event.getChannel();
    }
}