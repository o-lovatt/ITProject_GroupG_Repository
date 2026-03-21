package logic;

import java.util.ArrayList;
import java.util.List;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class AILegalActionGenerator {

    public static List<AIMoveAction> getLegalMoves(GameState gameState, Unit unit) {
        List<AIMoveAction> validMoves = new ArrayList<>();
        if (unit == null || unit.hasMoved()) return validMoves;

        int ux = unit.getPosition().getTilex();
        int uy = unit.getPosition().getTiley();

        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    Unit neighbor = gameState.getUnitAt(x, y);
                    if (neighbor != null && neighbor.getOwner() != unit.getOwner() && neighbor.isProvoke) {
                        return validMoves;
                    }
                }
            }
        }

        List<Tile> reachable = logic.MovementLogic.getReachableTiles(gameState, unit);
        for (Tile t : reachable) {
            validMoves.add(new AIMoveAction(unit, t));
        }

        return validMoves;
    }

    public static List<AIAttackAction> getLegalAttacks(GameState gameState, Unit unit) {
        List<AIAttackAction> validAttacks = new ArrayList<>();
        if (unit == null || unit.hasAttacked()) return validAttacks;

        int ux = unit.getPosition().getTilex();
        int uy = unit.getPosition().getTiley();

        List<Unit> adjacentProvokers = new ArrayList<>();
        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    Unit target = gameState.getUnitAt(x, y);
                    if (target != null && target.getOwner() != unit.getOwner() && target.isProvoke) {
                        adjacentProvokers.add(target);
                    }
                }
            }
        }

        if (!adjacentProvokers.isEmpty()) {
            for (Unit provoker : adjacentProvokers) {
                validAttacks.add(new AIAttackAction(unit, provoker));
            }
            return validAttacks;
        }

        for (int x = ux - 1; x <= ux + 1; x++) {
            for (int y = uy - 1; y <= uy + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    Unit target = gameState.getUnitAt(x, y);
                    if (target != null && target.getOwner() != unit.getOwner()) {
                        validAttacks.add(new AIAttackAction(unit, target));
                    }
                }
            }
        }
        return validAttacks;
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
