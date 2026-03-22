package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import java.util.List;
import java.util.ArrayList;
import structures.basic.Card;


public class AIController {

    public static void playAITurn(ActorRef out, GameState gameState) {
        BasicCommands.addPlayer1Notification(out, "AI Player's Turn", 2);

        new Thread(() -> {
            try {
                Thread.sleep(2000);

                boolean anyActionTaken = true;

                while (anyActionTaken && !gameState.isGameOver()) {
                    anyActionTaken = false;


                   //playing cards
                    for (int i = 0; i < gameState.player2.hand.size(); i++) {
                        Card card = gameState.player2.hand.get(i);

                        // skip if not enough mana
                        if (gameState.player2.getMana() < card.getManacost()) continue;

                        String unitConf = CardFactory.getUnitConfigByCardName(card.getCardname());

                        if (unitConf != null) {
                            //creature card
                            Tile summonTile = getEmptyTileNearAvatar(gameState, gameState.aiAvatar);
                            if (summonTile == null) continue; //no room to summon

                            System.out.println("[AI] Summoning: " + card.getCardname());
                            gameState.player2.setMana(gameState.player2.getMana() - card.getManacost());
                            BasicCommands.setPlayer2Mana(out, gameState.player2);
                            gameState.player2.hand.remove(i);

                            BasicCommands.playEffectAnimation(out,
                                    utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_summon), summonTile);
                            Thread.sleep(300);

                            Unit summoned = utils.BasicObjectBuilders.loadUnit(unitConf, card.getId(), Unit.class);
                            summoned.setPositionByTile(summonTile);
                            summoned.setOwner(2);

                            // BigCard stats
                            System.out.println("[AI] " + card.getCardname() + " bigCard attack="
                                    + (card.getBigCard() != null ? card.getBigCard().getAttack() : "null"));
                            if (card.getBigCard() != null) {
                                summoned.setHealth(card.getBigCard().getHealth());
                                summoned.setMaxHealth(card.getBigCard().getHealth());
                                summoned.setAttack(card.getBigCard().getAttack());
                                summoned.setAttackPower(card.getBigCard().getAttack());
                            } else {
                                summoned.setHealth(3);
                                summoned.setMaxHealth(3);
                                summoned.setAttack(2);
                                summoned.setAttackPower(2);
                            }

                            // apply keywords
                            applyAIUnitKeywords(summoned, card.getCardname());

                            gameState.placeUnit(summoned, summonTile.getTilex(), summonTile.getTiley());
                            BasicCommands.drawUnit(out, summoned, summonTile);
                            Thread.sleep(100);
                            BasicCommands.setUnitHealth(out, summoned, summoned.getHealth());
                            BasicCommands.setUnitAttack(out, summoned, summoned.getAttack());

                            // summoning sickness
                            summoned.setHasMoved(true);
                            if (!summoned.hasRush()) {
                                summoned.setHasAttacked(true);
                            }

                            anyActionTaken = true;
                            Thread.sleep(1500);
                            break;

                        } else {
                            // spell card
                            boolean spellCast = false;
                            String spellName = card.getCardname();

                            if (spellName.contains("True Strike")) {
                                //deal 2 damage to the human avatar
                                Unit target = gameState.humanAvatar;
                                if (target != null) {
                                    BasicCommands.playEffectAnimation(out,
                                            utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_inmolation),
                                            gameState.getTile(target.getPosition().getTilex(),
                                                    target.getPosition().getTiley()));
                                    Thread.sleep(400);
                                    SpellService.castTrueStrike(out, gameState, target);
                                    spellCast = true;
                                }

                            } else if (spellName.contains("Sundrop Elixir")) {
                                // heal the AI avatar
                                Unit target = gameState.aiAvatar;
                                if (target != null && target.getHealth() < target.getMaxHealth()) { //added this, doesn't waste spell if at full health now
                                    BasicCommands.playEffectAnimation(out,
                                            utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_buff),
                                            gameState.getTile(target.getPosition().getTilex(),
                                                    target.getPosition().getTiley()));
                                    Thread.sleep(400);
                                    SpellService.castSundropElixir(out, gameState, target);
                                    spellCast = true;
                                }

                            } else if (spellName.contains("Beam Shock")) {
                                // stun a random adjacent human unit

                                Unit target = findBeamShockTarget(gameState);
                                if (target != null) {
                                    SpellService.castBeamShock(out, gameState, target);
                                    spellCast = true;// otherwise stun the human avatar
                                }
                            }

                            if (spellCast) {
                                System.out.println("[AI] Cast spell: " + spellName);
                                gameState.player2.setMana(gameState.player2.getMana() - card.getManacost());
                                BasicCommands.setPlayer2Mana(out, gameState.player2);
                                gameState.player2.hand.remove(i);
                                anyActionTaken = true;
                                Thread.sleep(1500);
                                break;
                            }
                        }
                    }

                    if (anyActionTaken) continue;//back to while loop

                    //atacking

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
                            for (AIAttackAction attack : attacks) {
                                if (AIActionScorer.scoreAttack(attack) > AIActionScorer.scoreAttack(bestAttack)) {
                                    bestAttack = attack;
                                }
                            }//same fix from below applied here^^
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
                            for (AIMoveAction move : moves) {
                                if (AIActionScorer.scoreMove(move, gameState) > AIActionScorer.scoreMove(bestMove, gameState)) {
                                    bestMove = move;
                                }
                            }//stopped summoned units just moving all the way to the left

                            gameState.unitMoving = true;
                            BasicCommands.moveUnitToTile(out, u, bestMove.targetTile);
                            gameState.moveUnit(u, bestMove.targetTile.getTilex(), bestMove.targetTile.getTiley());
                            u.setHasMoved(true);
                            //gameState.unitMoving = false;
                            anyActionTaken = true;
                            //wait for animation tp finish fix
                            long timeout = System.currentTimeMillis() + 4000;
                            while(gameState.unitMoving && System.currentTimeMillis() < timeout){
                                Thread.sleep(100);
                            }
                            Thread.sleep(300); //wee buffer
                            break;
                        }
                    }
                }

                System.out.println("AI over the round");
                Thread.sleep(1000);

                gameState.getTurnManager().switchActivePlayer();
                gameState.getTurnManager().addMana();

                gameState.unitMoving = false;
                gameState.selectedUnit = null;
                gameState.selectedCardPosition = -1;

                for (int x = 1; x <= 9; x++) {
                    for (int y = 1; y <= 5; y++) {
                        Unit u = gameState.getUnitAt(x, y);
                        if (u != null) {
                            u.resetTurnState(); //small edit to include stun check

                        }
                    }
                }

                // vvvv addMana does this now
//                gameState.player1.maxMana = Math.min(gameState.player1.maxMana + 1, 9);
//                gameState.player2.maxMana = Math.min(gameState.player2.maxMana + 1, 9);
//                gameState.player1.setMana(gameState.player1.maxMana);

                gameState.player1.setMana(gameState.getHumanState().getMana());
                gameState.player2.setMana(gameState.getAiState().getMana());

                BasicCommands.setPlayer1Mana(out, gameState.player1);
                BasicCommands.setPlayer2Mana(out, gameState.player2);
                BasicCommands.addPlayer1Notification(out, "Your Turn!", 2);

                logic.HandService.drawCard(out, gameState.player1, true);

            } catch (Exception e) {
                e.printStackTrace();
                gameState.unitMoving = false;
            }
        }).start();
    }



    //card playing helpers
    private static Tile getEmptyTileNearAvatar(GameState gameState, Unit avatar) {
        if (avatar == null) return null;
        int ax = avatar.getPosition().getTilex();
        int ay = avatar.getPosition().getTiley();
        for (int x = ax - 1; x <= ax + 1; x++) {
            for (int y = ay - 1; y <= ay + 1; y++) {
                if (gameState.isInBounds(x, y) && gameState.getUnitAt(x, y) == null) {
                    return gameState.getTile(x, y);
                }
            }
        }
        return null;
    }

    private static void applyAIUnitKeywords(Unit unit, String cardName) {
        if (cardName == null) return;
        if (cardName.contains("Entangler") || cardName.contains("Knight")) {
            unit.setProvoke(true);
            unit.setZeal(true);
        } else if (cardName.contains("Guardian")) {
            unit.setProvoke(true);
        } else if (cardName.contains("Flamewing")) {
            unit.setFlying(true);
        } else if (cardName.contains("Tiger")) {
            unit.setRush(true);
        }
    }

    /** Pick a Beam Shock target: prefer non-avatar human units*/
    private static Unit findBeamShockTarget(GameState gameState) {
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit u = gameState.getUnitAt(x, y);
                if (u != null && u.getOwner() == 1 && u.getId() != 1) {
                    return u;
                }
            }
        }
        return gameState.humanAvatar;
    }
}