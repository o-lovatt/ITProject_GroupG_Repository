package logic;

import structures.GameState;
import structures.basic.Unit;

public class AIActionScorer {

    public static int scoreAttack(AIAttackAction action) {
        int score = 0;
        if (action.target != null && action.target.getId() == 1) {
            score += 1000;
        } else {
            score += 500;
        }
        return score;
    }

    public static int scoreMove(AIMoveAction action, GameState gameState) {
        Unit enemyAvatar = gameState.humanAvatar;
        if (enemyAvatar == null) return 0;

        int enemyX = enemyAvatar.getPosition().getTilex();
        int enemyY = enemyAvatar.getPosition().getTiley();

        int moveX = action.targetTile.getTilex();
        int moveY = action.targetTile.getTiley();

        int distance = Math.abs(enemyX - moveX) + Math.abs(enemyY - moveY);

        return 100 - (distance * 10);
    }
}