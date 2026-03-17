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

        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                if ((x != unitX || y != unitY) && gameState.isInBounds(x, y) && !gameState.hasUnitAt(x, y)) {

                    if (unit.isFlying()) {
                        reachable.add(gameState.getTile(x, y));
                    } else {
                        int distance = Math.abs(x - unitX) + Math.abs(y - unitY);
                        if (distance <= 2) {
                            reachable.add(gameState.getTile(x, y));
                        }
                    }
                }
            }
        }
        return reachable;
    }

    public static boolean isProvoked(GameState gameState, Unit unit) {
        int ux = unit.getPosition().getTilex();
        int uy = unit.getPosition().getTiley();

        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    Unit adjacentUnit = gameState.getUnitAt(x, y);

                    if (adjacentUnit != null && adjacentUnit.getId() != unit.getId() && adjacentUnit.hasProvoke()) {

                        boolean isEnemy = false;
                        if (unit.getId() == 1 && adjacentUnit.getId() == 2) isEnemy = true;
                        if (unit.getId() == 2 && adjacentUnit.getId() == 1) isEnemy = true;

                        if (isEnemy) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}