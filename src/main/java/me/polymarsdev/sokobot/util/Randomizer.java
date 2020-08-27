package me.polymarsdev.sokobot.util;

import java.util.*;

public class Randomizer{

    public static Random theInstance = null;

    public static Random getInstance(){
        if(theInstance == null){
            theInstance = new Random();
        }
        return theInstance;
    }

    /**
     * This method returns a random integer between 0 and n, exclusive.
     * @param n The maximum value for the range.
     * @return A random integer between 0 and n, exclusive.
     */
    public static int nextInt(int n){
        return Randomizer.getInstance().nextInt(n);
    }

}
