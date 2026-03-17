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

        boolean amIProvoked = MovementLogic.isProvoked(gameState, aiUnit);

        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    Unit target = gameState.getUnitAt(x, y);

                    if (target != null && target.getId() == 1) {

                        if (amIProvoked) {
                            if (target.hasProvoke()) {
                                attacks.add(new AIAttackAction(aiUnit, target));
                            }
                        } else {
                            attacks.add(new AIAttackAction(aiUnit, target));
                        }
                    }
                }
            }
        }
        return attacks;
    }

    public static List<AIPlayCardAction> getLegalCardPlays(GameState gameState) {
        List<AIPlayCardAction> cardPlays = new ArrayList<>();

        if (gameState.aiAvatar == null) return cardPlays;

        int avatarX = gameState.aiAvatar.getPosition().getTilex();
        int avatarY = gameState.aiAvatar.getPosition().getTiley();

        List<Tile> validSummonTiles = new ArrayList<>();
        for (int x = avatarX - 1; x <= avatarX + 1; x++) {
            for (int y = avatarY - 1; y <= avatarY + 1; y++) {
                if (gameState.isInBounds(x, y) && !gameState.hasUnitAt(x, y)) {
                    validSummonTiles.add(gameState.getTile(x, y));
                }
            }
        }

        // TODO: 遍历 AI 的手牌，如果法力值 (Mana) 足够，就为每个空地生成一个 AIPlayCardAction

        return cardPlays;
    }
}
