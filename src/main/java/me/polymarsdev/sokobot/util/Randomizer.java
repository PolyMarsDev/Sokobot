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
     * Return a random boolean value.
     * @return True or false value simulating a coin flip.
     */
    public static boolean nextBoolean(){
        return Randomizer.getInstance().nextBoolean();
    }

    /**
     * This method simulates a weighted coin flip which will return
     * true with the probability passed as a parameter.
     *
     * @param probability   The probability that the method returns true,
     * a value between 0 to 1 inclusive.
     * @return True or false value simulating a weighted coin flip.
     */
    public static boolean nextBoolean(double probability){
        return Randomizer.nextDouble() < probability;
    }

    /**
     * This method returns a random integer.
     * @return A random integer.
     */
    public static int nextInt(){
        return Randomizer.getInstance().nextInt();
    }

    /**
     * This method returns a random integer between 0 and n, exclusive.
     * @param n The maximum value for the range.
     * @return A random integer between 0 and n, exclusive.
     */
    public static int nextInt(int n){
        return Randomizer.getInstance().nextInt(n);
    }

    /**
     * Return a number between min and max, inclusive.
     * @param min   The minimum integer value of the range, inclusive.
     * @param max   The maximum integer value in the range, inclusive.
     * @return A random integer between min and max.
     */
    public static int nextInt(int min, int max){
        return min + Randomizer.nextInt(max - min + 1);
    }

    /**
     * Return a random double between 0 and 1.
     * @return A random double between 0 and 1.
     */
    public static double nextDouble(){
        return Randomizer.getInstance().nextDouble();
    }

    /**
     * Return a random double between min and max.
     * @param min The minimum double value in the range.
     * @param max The maximum double value in the rang.
     * @return A random double between min and max.
     */
    public static double nextDouble(double min, double max){
        return min + (max - min) * Randomizer.nextDouble();
    }

    /**
     * Return a random color.
     * @return A random hex string that represents a color.
     */
    public static String nextColor(){
        String randomNum = Integer.toHexString(Randomizer.nextInt(0, 16777216));
        return "'#" + randomNum + "'";
    }
}
