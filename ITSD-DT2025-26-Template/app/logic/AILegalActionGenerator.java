package logic;

import java.util.ArrayList;
import java.util.List;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class AILegalActionGenerator {

    public static List<AIMoveAction> getLegalMoves(GameState gameState, Unit aiUnit) {
        List<AIMoveAction> moves = new ArrayList<>();

        if (aiUnit.hasMoved()) {
            return moves;
        }

        List<Tile> reachable = MovementLogic.getReachableTiles(gameState, aiUnit);
        for (Tile t : reachable) {
            moves.add(new AIMoveAction(aiUnit, t));
        }

        return moves;
    }

    public static List<AIAttackAction> getLegalAttacks(GameState gameState, Unit aiUnit) {
        List<AIAttackAction> attacks = new ArrayList<>();

        if (aiUnit.hasAttacked()) {
            return attacks;
        }

        int ux = aiUnit.getPosition().getTilex();
        int uy = aiUnit.getPosition().getTiley();

        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    Unit target = gameState.getUnitAt(x, y);

                    if (target != null && target.getId() == 1) {
                        attacks.add(new AIAttackAction(aiUnit, target));
                    }
                }
            }
        }
        return attacks;
    }
}