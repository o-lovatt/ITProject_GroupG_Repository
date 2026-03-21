package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.Tile;
import structures.basic.Card;
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

                    for (int i = 0; i < gameState.player2.hand.size(); i++) {
                        Card card = gameState.player2.hand.get(i);
                        String unitConf = logic.CardFactory.getUnitConfigByCardName(card.getCardname());

                        if (gameState.player2.getMana() >= card.getManacost()) {

                            if (unitConf != null) {
                                Tile summonTile = getEmptyTileNearAvatar(gameState, gameState.aiAvatar);
                                if (summonTile != null) {
                                    System.out.println("AI summoned monster: " + card.getCardname());
                                    gameState.player2.setMana(gameState.player2.getMana() - card.getManacost());
                                    BasicCommands.setPlayer2Mana(out, gameState.player2);
                                    gameState.player2.hand.remove(i);

                                    BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_summon), summonTile);
                                    Thread.sleep(300);

                                    Unit summonedUnit = utils.BasicObjectBuilders.loadUnit(unitConf, card.getId(), Unit.class);
                                    summonedUnit.setPositionByTile(summonTile);
                                    summonedUnit.setOwner(2);

                                    if (card.getBigCard() != null) {
                                        summonedUnit.setHealth(card.getBigCard().getHealth());
                                        summonedUnit.setAttack(card.getBigCard().getAttack());
                                    } else {
                                        summonedUnit.setHealth(3);
                                        summonedUnit.setAttack(2);
                                    }

                                    gameState.placeUnit(summonedUnit, summonTile.getTilex(), summonTile.getTiley());
                                    BasicCommands.drawUnit(out, summonedUnit, summonTile);
                                    Thread.sleep(100);
                                    BasicCommands.setUnitHealth(out, summonedUnit, summonedUnit.getHealth());
                                    BasicCommands.setUnitAttack(out, summonedUnit, summonedUnit.getAttack());

                                    summonedUnit.setHasMoved(true);
                                    summonedUnit.setHasAttacked(true);

                                    anyActionTaken = true;
                                    Thread.sleep(2000);
                                    break;
                                }
                            }
                            else {
                                String spellName = card.getCardname();
                                boolean spellCast = false;

                                if (spellName.contains("True Strike")) {
                                    Unit target = gameState.humanAvatar;
                                    BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_inmolation), gameState.getTile(target.getPosition().getTilex(), target.getPosition().getTiley()));
                                    Thread.sleep(400);

                                    target.setHealth(target.getHealth() - 2);
                                    BasicCommands.setUnitHealth(out, target, target.getHealth());
                                    gameState.player1.setHealth(target.getHealth());
                                    BasicCommands.setPlayer1Health(out, gameState.player1);
                                    spellCast = true;
                                }
                                else if (spellName.contains("Sundrop Elixir")) {
                                    Unit target = gameState.aiAvatar;
                                    BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_buff), gameState.getTile(target.getPosition().getTilex(), target.getPosition().getTiley()));
                                    Thread.sleep(400);

                                    target.setHealth(Math.min(target.getHealth() + 5, 20));
                                    BasicCommands.setUnitHealth(out, target, target.getHealth());
                                    gameState.player2.setHealth(target.getHealth());
                                    BasicCommands.setPlayer2Health(out, gameState.player2);
                                    spellCast = true;
                                }
                                else if (spellName.contains("Beam Shock")) {
                                    spellCast = true;
                                }

                                if (spellCast) {
                                    System.out.println("AI cast spell: " + spellName);
                                    gameState.player2.setMana(gameState.player2.getMana() - card.getManacost());
                                    BasicCommands.setPlayer2Mana(out, gameState.player2);
                                    gameState.player2.hand.remove(i);

                                    anyActionTaken = true;
                                    Thread.sleep(1500);
                                    break;
                                }
                            }
                        }
                    }

                    if (anyActionTaken) continue;

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

                System.out.println("AI Turn Completed.");
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

    private static Tile getEmptyTileNearAvatar(GameState gameState, Unit avatar) {
        int ax = avatar.getPosition().getTilex();
        int ay = avatar.getPosition().getTiley();
        for (int x = ax - 1; x <= ax + 1; x++) {
            for (int y = ay - 1; y <= ay + 1; y++) {
                if (gameState.isInBounds(x, y)) {
                    if (gameState.getUnitAt(x, y) == null) {
                        return gameState.getTile(x, y);
                    }
                }
            }
        }
        return null;
    }
}