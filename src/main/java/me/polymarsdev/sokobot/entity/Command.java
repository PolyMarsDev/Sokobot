package me.polymarsdev.sokobot.entity;

import me.polymarsdev.sokobot.event.CommandEvent;

public abstract class Command {

    private final String name;

    public Command(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(CommandEvent commandEvent);
}
