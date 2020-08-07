package me.polymarsdev.sokobot.objects;

public class Destination {
    int x = 0;
    int y = 0;
    Grid currentGrid;

    public Destination(int x, int y, Grid currentGrid) {
        this.x = x;
        this.y = y;
        this.currentGrid = currentGrid;
    }

    public boolean hasBox(Grid currentGrid) {
        return currentGrid.isWall(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
