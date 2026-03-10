package logic;

import structures.basic.Position;
import structures.basic.Tile;

public class ActionValidator {

    /**
     * Checks if a movement from a start position to an end tile is legal.
     * Legal movement: Up to 2 tiles orthogonally or 1 tile diagonally.
     */
    public static boolean isValidMovement(Position start, Tile endTile) {
        int dx = Math.abs(start.getTilex() - endTile.getTilex());
        int dy = Math.abs(start.getTiley() - endTile.getTiley());

        // 1 tile diagonally
        if (dx == 1 && dy == 1) return true;
        // Up to 2 tiles straight up, down, left, or right
        if ((dx <= 2 && dy == 0) || (dy <= 2 && dx == 0)) return true;

        return false;
    }

    /**
     * Checks if a target is within valid attack range.
     * Legal attack: 1 tile in any direction (including diagonal).
     */
    public static boolean isWithinAttackRange(Position attackerPos, Position targetPos) {
        int dx = Math.abs(attackerPos.getTilex() - targetPos.getTilex());
        int dy = Math.abs(attackerPos.getTiley() - targetPos.getTiley());

        // Cannot attack oneself (dx=0, dy=0), but can attack anything 1 tile away
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }
}