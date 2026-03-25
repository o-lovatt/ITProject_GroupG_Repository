package logic;

import java.util.ArrayList;
import java.util.List;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class MovementLogic {

    public static List<Tile> getReachableTiles(GameState gameState, Unit unit) {
        List<Tile> reachable = new ArrayList<>();
        if (isProvoked(gameState, unit)) {
            return reachable;
        }
        int unitX = unit.getPosition().getTilex();
        int unitY = unit.getPosition().getTiley();

        if(unit.isFlying()) {//flying units can go to any empty tile
            for (int x = 1; x <= 9; x++) {
                for (int y = 1; y <= 5; y++) {
                    if (x == unitX && y == unitY) continue;
                    if (!gameState.hasUnitAt(x, y)) {
                        reachable.add(gameState.getTile(x, y));
                    }
                }
            }
            return reachable;
        }
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};//for non flying check the path isn;t blocked

        for(int dx = -1; dx <= 1; dx++){ //1 tile move, always valid if empty
            for(int dy = -1; dy <= 1; dy++){
                if(dx == 0 && dy == 0)
                    continue;
                int nx = unitX + dx;
                int ny = unitY + dy;
                if(gameState.isInBounds(nx, ny) && !gameState.hasUnitAt(nx, ny)){
                    reachable.add(gameState.getTile(nx, ny));
                }
            }
        }
        for(int[] direction : directions){ //2 tile move
            int midX = unitX + direction[0];
            int midY = unitY + direction[1];
            int endX = unitX + direction[0]*2;
            int endY = unitY + direction[1]*2;


            if(!gameState.isInBounds(midX, midY))
                continue;
            if(!gameState.isInBounds(endX, endY))
                continue;
            if(gameState.hasUnitAt(midX, midY))
                continue;
            if(gameState.hasUnitAt(endX, endY))
                continue;
            reachable.add(gameState.getTile(endX, endY));

        }
        return reachable;

    }

    public static boolean isProvoked(GameState gameState, Unit unit) {
        int ux = unit.getPosition().getTilex();
        int uy = unit.getPosition().getTiley();

        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (x == ux && y == uy) continue;
                if (!gameState.isInBounds(x, y)) continue;

                Unit adjacentUnit = gameState.getUnitAt(x, y);

                if (adjacentUnit != null
                        && adjacentUnit.getOwner() != unit.getOwner()
                        && adjacentUnit.hasProvoke()) {
                    return true;
                }
            }
        }
        return false;
    }
}