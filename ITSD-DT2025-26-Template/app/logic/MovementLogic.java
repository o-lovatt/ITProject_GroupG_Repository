package logic;

import java.util.ArrayList;
import java.util.List;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class MovementLogic {

    public static List<Tile> getReachableTiles(GameState gameState, Unit unit) {
        List<Tile> reachable = new ArrayList<>();

        int unitX = unit.getPosition().getTilex();
        int unitY = unit.getPosition().getTiley();

        for (int x = unitX - 2; x <= unitX + 2; x++) {
            for (int y = unitY - 2; y <= unitY + 2; y++) {

                if (gameState.isInBounds(x, y)) {

                    int distance = Math.abs(x - unitX) + Math.abs(y - unitY);

                    if (distance <= 2 && distance > 0) {
                        if (!gameState.hasUnitAt(x, y)) {
                            reachable.add(gameState.getTile(x, y));
                        }
                    }
                }
            }
        }
        return reachable;
    }
}