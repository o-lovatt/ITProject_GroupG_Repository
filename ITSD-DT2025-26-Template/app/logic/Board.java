package logic;

import structures.basic.Unit;

public class Board {
    // A 2D array to act as our grid memory (9 columns, 5 rows)
    private Unit[][] grid;

    public Board() {
        grid = new Unit[9][5];
    }

    // Put a unit onto a specific coordinate
    public void placeUnit(Unit unit, int x, int y) {
        if (x >= 0 && x < 9 && y >= 0 && y < 5) {
            grid[x][y] = unit;
        }
    }

    // Remove a unit from a coordinate
    public void removeUnit(int x, int y) {
        if (x >= 0 && x < 9 && y >= 0 && y < 5) {
            grid[x][y] = null;
        }
    }

    // Ask the board: "Who is standing here?"
    public Unit getUnit(int x, int y) {
        if (x >= 0 && x < 9 && y >= 0 && y < 5) {
            return grid[x][y];
        }
        return null;
    }
}