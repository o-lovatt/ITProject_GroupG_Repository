package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import java.util.List;
import java.util.ArrayList;

public class AIController {

    public static void playAITurn(ActorRef out, GameState gameState) {
        BasicCommands.addPlayer1Notification(out, "AI is thinking...", 2);

        new Thread(() -> {
            try {
                Thread.sleep(2000);

                boolean anyActionTaken = true;

                while (anyActionTaken && !gameState.isGameOver()) {
                    anyActionTaken = false;

                    List<Unit> aiUnits = new ArrayList<>();
                    for (int x = 1; x <= 9; x++) {
                        for (int y = 1; y <= 5; y++) {
                            Unit u = gameState.getUnitAt(x, y);
                            if (u != null && u.getOwner() == 2) {
                                aiUnits.add(u);
                            }
                        }
                    }

                    for (Unit u : aiUnits) {
                        List<AIAttackAction> attacks = AILegalActionGenerator.getLegalAttacks(gameState, u);
                        if (!attacks.isEmpty() && !u.hasAttacked()) {
                            AIAttackAction bestAttack = attacks.get(0);
                            new CombatResolver().executeAttack(out, gameState, u, bestAttack.target);
                            u.setHasAttacked(true);
                            u.setHasMoved(true);
                            anyActionTaken = true;
                            Thread.sleep(2500);
                            break;
                        }

                        List<AIMoveAction> moves = AILegalActionGenerator.getLegalMoves(gameState, u);
                        if (!moves.isEmpty() && !u.hasMoved()) {
                            AIMoveAction bestMove = moves.get(0);
                            gameState.unitMoving = true;
                            BasicCommands.moveUnitToTile(out, u, bestMove.targetTile);
                            gameState.moveUnit(u, bestMove.targetTile.getTilex(), bestMove.targetTile.getTiley());
                            u.setHasMoved(true);
                            gameState.unitMoving = false;
                            anyActionTaken = true;
                            Thread.sleep(2500);
                            break;
                        }
                    }
                }

                System.out.println("AI over the round");
                Thread.sleep(1000);

                gameState.getTurnManager().endTurn();

                gameState.unitMoving = false;
                gameState.selectedUnit = null;
                gameState.selectedCardPosition = -1;

                for (int x = 1; x <= 9; x++) {
                    for (int y = 1; y <= 5; y++) {
                        Unit u = gameState.getUnitAt(x, y);
                        if (u != null) {
                            u.setHasMoved(false);
                            u.setHasAttacked(false);
                        }
                    }
                }

                gameState.player1.maxMana = Math.min(gameState.player1.maxMana + 1, 9);
                gameState.player2.maxMana = Math.min(gameState.player2.maxMana + 1, 9);
                gameState.player1.setMana(gameState.player1.maxMana);

                BasicCommands.setPlayer1Mana(out, gameState.player1);
                BasicCommands.setPlayer2Mana(out, gameState.player2);
                BasicCommands.addPlayer1Notification(out, "Your Turn!", 2);

                logic.HandService.drawCard(out, gameState.player1, true);

            } catch (Exception e) {
                e.printStackTrace();
                gameState.unitMoving = false;
                gameState.getTurnManager().endTurn();
            }
        }).start();
    }
}