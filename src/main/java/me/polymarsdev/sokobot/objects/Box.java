package me.polymarsdev.sokobot.objects;

public class Box
{
    int x = 0;
    int y = 0;
    int originalX = 0; //in case player messes up and wants to reset
    int originalY = 0;
    Grid currentGrid;
    public Box(int x, int y, Grid currentGrid)
    {
        this.x = x;
        this.y = y;
        originalX = x;
        originalY = y;
        this.currentGrid = currentGrid;
    }
    public void reset()
    {
        x = originalX;
        y = originalY;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public boolean moveUp()
    {
        if (!currentGrid.isWall(x, y - 1) && !currentGrid.isBox(x, y - 1))
        {
            y -= 1;
            return true;
        }
        return false;
    }
    public boolean moveDown()
    {
        if (!currentGrid.isWall(x, y + 1) && !currentGrid.isBox(x, y + 1))
        {
            y += 1;
            return true;
        }
        return false;
    }
    public boolean moveLeft()
    {
        if (!currentGrid.isWall(x - 1, y) && !currentGrid.isBox(x - 1, y))
        {
            x -= 1;
            return true;
        }
        return false;
    }
    public boolean moveRight()
    {
        if (!currentGrid.isWall(x + 1, y) && !currentGrid.isBox(x + 1, y))
        {
            x += 1;
            return true;
        }
        return false;
    }
    public boolean onDestination()
    {
        return currentGrid.isDestination(x, y);
    }

}
