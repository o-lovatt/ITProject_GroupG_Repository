package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import java.util.List;

public class AIController {

    public static void playAITurn(ActorRef out, GameState gameState) {
        BasicCommands.addPlayer1Notification(out, "AI is thinking...", 2);

        new Thread(() -> {
            try {
                Thread.sleep(1500);

                Unit aiAvatar = gameState.aiAvatar;
                boolean actionTaken = true;

                while (actionTaken && !gameState.isGameOver()) {
                    actionTaken = false;

                    List<AIAttackAction> attacks = AILegalActionGenerator.getLegalAttacks(gameState, aiAvatar);
                    if (!attacks.isEmpty()) {
                        AIAttackAction bestAttack = attacks.get(0);
                        int maxScore = AIActionScorer.scoreAttack(bestAttack);

                        for (AIAttackAction attack : attacks) {
                            int score = AIActionScorer.scoreAttack(attack);
                            if (score > maxScore) {
                                bestAttack = attack;
                                maxScore = score;
                            }
                        }

                        CombatResolver combat = new CombatResolver();
                        combat.executeAttack(out, gameState, bestAttack.attacker, bestAttack.target);
                        actionTaken = true;
                        Thread.sleep(2000);
                        continue;
                    }

                    List<AIMoveAction> moves = AILegalActionGenerator.getLegalMoves(gameState, aiAvatar);
                    if (!moves.isEmpty()) {
                        AIMoveAction bestMove = moves.get(0);
                        int maxScore = AIActionScorer.scoreMove(bestMove, gameState);

                        for (AIMoveAction move : moves) {
                            int score = AIActionScorer.scoreMove(move, gameState);
                            if (score > maxScore) {
                                bestMove = move;
                                maxScore = score;
                            }
                        }

                        BasicCommands.moveUnitToTile(out, aiAvatar, bestMove.targetTile);
                        gameState.moveUnit(aiAvatar, bestMove.targetTile.getTilex(), bestMove.targetTile.getTiley());
                        aiAvatar.setHasMoved(true);
                        actionTaken = true;

                        Thread.sleep(2000);
                        continue;
                    }
                }

                System.out.println("AI over the round");
                aiAvatar.resetTurnState();

                gameState.getTurnManager().endTurn();
                BasicCommands.setPlayer1Mana(out, gameState.player1);
                BasicCommands.setPlayer2Mana(out, gameState.player2);
                BasicCommands.addPlayer1Notification(out, "Your Turn!", 2);

            } catch (Exception e) {
                e.printStackTrace();
                gameState.getTurnManager().endTurn();
            }
        }).start();
    }
}