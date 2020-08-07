package me.polymarsdev.sokobot.event;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandEvent {
    private static final int MAX_MESSAGES = 2;

    private final GuildMessageReceivedEvent event;
    private String[] args;

    public CommandEvent(GuildMessageReceivedEvent event, String[] args) {
        this.event = event;
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    public List<Member> getMentionedMembers() {
        List<Member> mentionedMembers = new ArrayList<>(event.getMessage().getMentionedMembers());
        if (event.getMessage().getContentRaw().startsWith("<@!" + event.getJDA().getSelfUser().getId() + ">"))
            mentionedMembers.remove(0);
        return mentionedMembers;
    }

    public void reply(String message) {
        sendMessage(event.getChannel(), message);
    }

    public void reply(String message, Consumer<Message> success) {
        sendMessage(event.getChannel(), message, success);
    }

    public void reply(String message, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMessage(event.getChannel(), message, success, failure);
    }

    public void reply(MessageEmbed embed) {
        event.getChannel().sendMessage(embed).queue();
    }

    public void reply(MessageEmbed embed, Consumer<Message> success) {
        event.getChannel().sendMessage(embed).queue(success);
    }

    public void reply(MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure) {
        event.getChannel().sendMessage(embed).queue(success, failure);
    }

    public void reply(Message message) {
        event.getChannel().sendMessage(message).queue();
    }

    public void reply(Message message, Consumer<Message> success) {
        event.getChannel().sendMessage(message).queue(success);
    }

    public void reply(Message message, Consumer<Message> success, Consumer<Throwable> failure) {
        event.getChannel().sendMessage(message).queue(success, failure);
    }

    public void reply(File file, String filename) {
        event.getChannel().sendFile(file, filename).queue();
    }

    public void reply(String message, File file, String filename) {
        String msg = message == null ? null : splitMessage(message).get(0);
        if (msg == null) event.getChannel().sendFile(file, filename).queue();
        else event.getChannel().sendMessage(msg).addFile(file, filename).queue();
    }

    private void sendMessage(MessageChannel chan, String message) {
        ArrayList<String> messages = splitMessage(message);
        for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
            chan.sendMessage(messages.get(i)).queue();
        }
    }

    private void sendMessage(MessageChannel chan, String message, Consumer<Message> success) {
        ArrayList<String> messages = splitMessage(message);
        for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
            if (i + 1 == MAX_MESSAGES || i + 1 == messages.size()) {
                chan.sendMessage(messages.get(i)).queue(success);
            } else {
                chan.sendMessage(messages.get(i)).queue();
            }
        }
    }

    private void sendMessage(MessageChannel chan, String message, Consumer<Message> success,
                             Consumer<Throwable> failure) {
        ArrayList<String> messages = splitMessage(message);
        for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
            if (i + 1 == MAX_MESSAGES || i + 1 == messages.size()) {
                chan.sendMessage(messages.get(i)).queue(success, failure);
            } else {
                chan.sendMessage(messages.get(i)).queue();
            }
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

    SelfUser getSelfUser() {
        return event.getJDA().getSelfUser();
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public JDA getJDA() {
        return event.getJDA();
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