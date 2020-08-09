package me.polymarsdev.sokobot.entity;

import me.polymarsdev.sokobot.objects.Grid;

public class Player {
    int x = 0;
    int y = 0;
    Grid currentGrid;

    public Player(int x, int y, Grid currentGrid) {
        this.x = x;
        this.y = y;
        this.currentGrid = currentGrid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void resetPosition() {
        int setX = 2;
        int setY = 2;
        while (currentGrid.isBoxRaw(setX, setY)) {
            if (setX >= currentGrid.getWidth() - 1) {
                setY++;
                setX = 1;
            } else setX++;
        }
        this.x = setX;
        this.y = setY;
    }

    public boolean moveUp() {
        if (!currentGrid.isWall(x, y - 1)) {
            if (currentGrid.isBox(x, y - 1)) {
                if (currentGrid.getBox(x, y - 1).moveUp()) {
                    y -= 1;
                    return true;
                }
                return false;
            }
            y -= 1;
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if (!currentGrid.isWall(x, y + 1)) {
            if (currentGrid.isBox(x, y + 1)) {
                if (currentGrid.getBox(x, y + 1).moveDown()) {
                    y += 1;
                    return true;
                }
                return false;
            }
            y += 1;
            return true;
        }
        return false;
    }

    public boolean moveLeft() {
        if (!currentGrid.isWall(x - 1, y)) {
            if (currentGrid.isBox(x - 1, y)) {
                if (currentGrid.getBox(x - 1, y).moveLeft()) {
                    x -= 1;
                    return true;
                }
                return false;
            }
            x -= 1;
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        if (!currentGrid.isWall(x + 1, y)) {
            if (currentGrid.isBox(x + 1, y)) {
                if (currentGrid.getBox(x + 1, y).moveRight()) {
                    x += 1;
                    return true;
                }
                return false;
            }
            x += 1;
            return true;
        }
        return false;
    }

}
