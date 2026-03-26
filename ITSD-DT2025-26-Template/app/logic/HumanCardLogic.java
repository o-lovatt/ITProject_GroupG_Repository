package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.PlayerSide;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.*;

public class HumanCardLogic {

    private static final Random RNG = new Random();

    // Tracks the card names of units on the board for Deathwatch and Opening Gambit triggers.
    private static final Map<Integer, String> UNIT_CARD_NAMES = new HashMap<>();

    // Human avatar Horn robustness
    private static int humanHornRobustness = 0;

    private HumanCardLogic() {
    }


    public static boolean handleCardClicked(ActorRef out, GameState gameState, int handPosition, Card clickedCard) {
        System.out.println("DEBUG handleCardClicked: card=" + clickedCard.getCardname()
                + " isCreature=" + clickedCard.isCreature()
                + " mana=" + gameState.player1.getMana());
        /// DEBUG

        if (clickedCard == null) return false;
        if (!isHumanCard(clickedCard.getCardname())) return false;

        clearHighlights(out, gameState);

        // no, direct cast
        if (isImmediateSpell(clickedCard.getCardname())) {
            if (!hasEnoughMana(out, gameState, clickedCard)) {
                gameState.selectedCardPosition = -1;
                return true;
            }

            if ("Wraithling Swarm".equals(clickedCard.getCardname())) {
                spendManaAndRemoveCard(out, gameState, clickedCard);
                castWraithlingSwarm(out, gameState);
                gameState.selectedCardPosition = -1;
                return true;
            }

            if ("Horn of the Forsaken".equals(clickedCard.getCardname())) {
                spendManaAndRemoveCard(out, gameState, clickedCard);
                humanHornRobustness = 3;
                BasicCommands.addPlayer1Notification(out, "Horn of the Forsaken equipped (3)", 2);
                gameState.selectedCardPosition = -1;
                return true;
            }
        }

        // yes, highlight
        if ("Dark Terminus".equals(clickedCard.getCardname())) {
            highlightEnemyCreatures(out, gameState);
            return true;
        }

        // Creature：highlight
        highlightSummonTiles(out, gameState);
        return true;
    }

    public static boolean handleTileClicked(ActorRef out, GameState gameState, int tilex, int tiley) {
//        System.out.println("DEBUG handleTileClicked: selectedPos=" + gameState.selectedCardPosition
//                + " tile=(" + tilex + "," + tiley + ")"
//                + " highlighted=" + gameState.isHighlighted(tilex, tiley));
//        /// DEBUG

        if (gameState.selectedCardPosition <= 0) return false;

        Card card = gameState.player1.getCardByHandPosition(gameState.selectedCardPosition);
        if (card == null) return false;
        if (!isHumanCard(card.getCardname())) return false;

        if (!gameState.isHighlighted(tilex, tiley)) {
            return false;
        }

        if (!hasEnoughMana(out, gameState, card)) {
            clearHighlights(out, gameState);
            gameState.selectedCardPosition = -1;
            return true;
        }

        clearHighlights(out, gameState);

        if (card.isCreature()) {
            if(gameState.hasUnitAt(tilex, tiley)){//guard to stop summoning on top of other units
                clearHighlights(out, gameState);
                gameState.selectedCardPosition = -1;
                BasicCommands.addPlayer1Notification(out, "Tile is occupied!", 2);
                return true;
            }
            spendManaAndRemoveCard(out, gameState, card);
            summonHumanCreature(out, gameState, card, tilex, tiley);
            gameState.selectedCardPosition = -1;
            return true;
        }

        if ("Dark Terminus".equals(card.getCardname())) {
            Unit target = gameState.getUnitAt(tilex, tiley);
            if (target != null && target.getOwner() == 2 && target.getId() != 2) {
                spendManaAndRemoveCard(out, gameState, card);
                castDarkTerminus(out, gameState, target);
            }
            gameState.selectedCardPosition = -1;
            return true;
        }

        return false;
    }

    public static void handleUnitDeath(ActorRef out, GameState gameState, Unit deadUnit) {
        if (deadUnit == null) return;

        List<Unit> boardUnits = getAllBoardUnits(gameState);

        for (Unit unit : boardUnits) {
            String cardName = UNIT_CARD_NAMES.get(unit.getId());
            if (cardName == null) continue;

            switch (cardName) {
                case "Bad Omen":
                    buffAttack(out, unit, 1);
                    break; //minor fixes

                case "Shadow Watcher":
                    buffAttack(out, unit, 1);
                    unit.setMaxHealth(unit.getMaxHealth() + 1);
                    unit.setHealth(unit.getHealth() + 1);
                    BasicCommands.setUnitHealth(out, unit, unit.getHealth());
                    break; //fix

                case "Bloodmoon Priestess":
                    summonRandomAdjacentWraithling(out, gameState, unit, unit.getOwner());
                    break;

                case "Shadowdancer":
                    Unit enemyAvatar = getEnemyAvatar(gameState, unit.getOwner());
                    if (enemyAvatar != null) {
                        enemyAvatar.takeDamage(1);
                        updateAvatarUi(out, gameState, enemyAvatar);

                        //if shadowdancer kills the enemy avatar, end the game
                        if (enemyAvatar.isDead()) { //does this need a null point exception?
                            BasicCommands.deleteUnit(out, enemyAvatar);
                            gameState.removeUnit(enemyAvatar.getPosition().getTilex(), enemyAvatar.getPosition().getTiley());
                            if (enemyAvatar.getId() == 2) {
                                BasicCommands.addPlayer1Notification(out, "GAME OVER - YOU WIN", 100);
                                gameState.setGameOver(PlayerSide.HUMAN_LEFT);
                            } else if (enemyAvatar.getId() == 1) {
                                BasicCommands.addPlayer1Notification(out, "GAME OVER - YOU LOOSE", 100);
                                gameState.setGameOver(PlayerSide.AI_RIGHT);
                            }
                        }
                        unit.heal(1);
                        BasicCommands.setUnitHealth(out, unit, unit.getHealth());

                    }
                    break;
            }
        }
    }

    public static void handleAvatarDamaged(ActorRef out, GameState gameState, Unit damagedUnit) {
        if (damagedUnit == null) return;

        // Human's Horn
        if (damagedUnit.getId() == 1 && humanHornRobustness > 0) {
            summonRandomAdjacentWraithling(out, gameState, damagedUnit, 1);

            humanHornRobustness--;
            if (humanHornRobustness <= 0) {
                humanHornRobustness = 0;
                BasicCommands.addPlayer1Notification(out, "Horn of the Forsaken broke", 2);
            }
        }
    }

    public static void handleAfterAttackDamage(ActorRef out, GameState gameState, Unit attacker, Unit target) {
        //if (attacker == null || target == null) return;

        // Horn: avatar get damage, create a Wraithling
        //if (attacker.getId() == 1 && attacker.getOwner() == 1 && target.getOwner() == 2 && humanHornRobustness > 0) {
            //HORN OF FORSAKEN NOW HANDLED IN handleAvatarDamage
    }

    // ========= Human card effects =========

    private static void castWraithlingSwarm(ActorRef out, GameState gameState) {
        Unit avatar = getHumanAvatar(gameState);
        if (avatar == null) return;

        for (int i = 0; i < 3; i++) {
            summonRandomAdjacentWraithling(out, gameState, avatar, 1);
        }
    }

    private static void castDarkTerminus(ActorRef out, GameState gameState, Unit target) {
        int x = target.getPosition().getTilex();
        int y = target.getPosition().getTiley();

        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.death);
        sleep(700);
        BasicCommands.deleteUnit(out, target);
        gameState.removeUnit(x, y);

        handleUnitDeath(out, gameState, target);

        Unit wraithling = HumanTokenFactory.createWraithling(1);
        gameState.placeUnit(wraithling, x, y);
        BasicCommands.drawUnit(out, wraithling, gameState.getTile(x, y));
        sleep(100);
        BasicCommands.setUnitHealth(out, wraithling, wraithling.getHealth());
        BasicCommands.setUnitAttack(out, wraithling, wraithling.getAttack());
        UNIT_CARD_NAMES.put(wraithling.getId(), "Wraithling");
    }

    private static void summonHumanCreature(ActorRef out, GameState gameState, Card card, int tilex, int tiley) {
        String conf = getHumanUnitConfig(card.getCardname());
        if (conf == null) return;

        Tile targetTile = gameState.getTile(tilex, tiley);
        BasicCommands.playEffectAnimation(out, BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon), targetTile);

        Unit unit = BasicObjectBuilders.loadUnit(conf, card.getId(), Unit.class);
        unit.setOwner(1);
        unit.setPositionByTile(targetTile);

        applyBaseStats(unit, card.getCardname());
        gameState.placeUnit(unit, tilex, tiley);

        BasicCommands.drawUnit(out, unit, targetTile);
        sleep(100);
        BasicCommands.setUnitHealth(out, unit, unit.getHealth());
        BasicCommands.setUnitAttack(out, unit, unit.getAttack());

        // summoning sickness
        unit.setHasMoved(true);
        if (!unit.hasRush()) {
            unit.setHasAttacked(true);
        }

        UNIT_CARD_NAMES.put(unit.getId(), card.getCardname());

        triggerOpeningGambit(out, gameState, unit, card.getCardname());
    }

    private static void triggerOpeningGambit(ActorRef out, GameState gameState, Unit unit, String cardName) {
        if ("Gloom Chaser".equals(cardName)) {
            int x = unit.getPosition().getTilex() - 1; // human left
            int y = unit.getPosition().getTiley();
            if (gameState.isInBounds(x, y) && !gameState.hasUnitAt(x, y)) {
                summonWraithlingAt(out, gameState, x, y, 1);
            }
        } else if ("Nightsorrow Assassin".equals(cardName)) {
            int ux = unit.getPosition().getTilex();
            int uy = unit.getPosition().getTiley();

            for (int x = ux - 1; x <= ux + 1; x++) {
                for (int y = uy - 1; y <= uy + 1; y++) {
                    if (!gameState.isInBounds(x, y) || (x == ux && y == uy)) continue;

                    Unit target = gameState.getUnitAt(x, y); //nightsorrow could kill the avatar, added && target.getId() != 2
                    if (target != null && target.getOwner() == 2 && target.getId() != 2 && target.getHealth() < target.getMaxHealth()) {
                        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.death);
                        sleep(700);
                        BasicCommands.deleteUnit(out, target);
                        gameState.removeUnit(x, y);
                        handleUnitDeath(out, gameState, target);
                        gameState.checkWinner();
                        return;
                    }
                }
            }
        }
    }


    //tile highlighting fixes
    //this should highlight empty tiles next to frienly players not just the avatar
    private static void highlightSummonTiles(ActorRef out, GameState gameState) {
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit u = gameState.getUnitAt(x, y);
                if (u != null && u.getOwner() == 1) {
                    //check 8 adjacents
                    for (int nx = x - 1; nx <= x + 1; nx++) {
                        for (int ny = y - 1; ny <= y + 1; ny++) {
                            if (nx == x && ny == y)
                                continue;
                            if (gameState.isInBounds(nx, ny) && !gameState.hasUnitAt(nx, ny) && !gameState.isHighlighted(nx, ny)) {
                                gameState.addHighlight(nx, ny);
                                BasicCommands.drawTile(out, gameState.getTile(nx, ny), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void highlightEnemyCreatures(ActorRef out, GameState gameState) {
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit target = gameState.getUnitAt(x, y);
                //dark terminus only targets non avatar creatures
                if (target != null && target.getOwner() == 2 && target.getId() != 2) {
                    gameState.addHighlight(x, y);
                    BasicCommands.drawTile(out, gameState.getTile(x, y), 2);
                }
            }
        }
    }

    // ========= helpers =========

    private static boolean isHumanCard(String name) {
        return "Bad Omen".equals(name)
                || "Gloom Chaser".equals(name)
                || "Rock Pulveriser".equals(name)
                || "Shadow Watcher".equals(name)
                || "Nightsorrow Assassin".equals(name)
                || "Bloodmoon Priestess".equals(name)
                || "Shadowdancer".equals(name)
                || "Horn of the Forsaken".equals(name)
                || "Wraithling Swarm".equals(name)
                || "Dark Terminus".equals(name);
    }

    private static boolean isImmediateSpell(String name) {
        return "Wraithling Swarm".equals(name) || "Horn of the Forsaken".equals(name);
    }

    private static String getHumanUnitConfig(String cardName) {
        if ("Bad Omen".equals(cardName)) return "conf/gameconfs/units/bad_omen.json";
        if ("Gloom Chaser".equals(cardName)) return "conf/gameconfs/units/gloom_chaser.json";
        if ("Rock Pulveriser".equals(cardName)) return "conf/gameconfs/units/rock_pulveriser.json";
        if ("Shadow Watcher".equals(cardName)) return "conf/gameconfs/units/shadow_watcher.json";
        if ("Nightsorrow Assassin".equals(cardName)) return "conf/gameconfs/units/nightsorrow_assassin.json";
        if ("Bloodmoon Priestess".equals(cardName)) return "conf/gameconfs/units/bloodmoon_priestess.json";
        if ("Shadowdancer".equals(cardName)) return "conf/gameconfs/units/shadowdancer.json";
        return null;
    }

    private static void applyBaseStats(Unit unit, String cardName) {
        int attack = 0;
        int health = 1;

        if ("Bad Omen".equals(cardName)) {
            attack = 0;
            health = 1;
        } else if ("Gloom Chaser".equals(cardName)) {
            attack = 3;
            health = 1;
        } else if ("Rock Pulveriser".equals(cardName)) {
            attack = 1;
            health = 4;
            unit.setProvoke(true);
        } else if ("Shadow Watcher".equals(cardName)) {
            attack = 3;
            health = 2;
        } else if ("Nightsorrow Assassin".equals(cardName)) {
            attack = 4;
            health = 2;
        } else if ("Bloodmoon Priestess".equals(cardName)) {
            attack = 3;
            health = 3;
        } else if ("Shadowdancer".equals(cardName)) {
            attack = 5;
            health = 4;
        }

        unit.setAttack(attack);
        unit.setAttackPower(attack);
        unit.setHealth(health);
        unit.setMaxHealth(health);
    }

    private static boolean hasEnoughMana(ActorRef out, GameState gameState, Card card) {
        if (gameState.player1.getMana() < card.getManacost()) {
            BasicCommands.addPlayer1Notification(out, "Not enough Mana!", 2);
            return false;
        }
        return true;
    }

    private static void spendManaAndRemoveCard(ActorRef out, GameState gameState, Card card) {
        gameState.player1.setMana(gameState.player1.getMana() - card.getManacost());
        BasicCommands.setPlayer1Mana(out, gameState.player1);

        gameState.player1.hand.remove(card);
        redrawHand(out, gameState);
    }

    private static void redrawHand(ActorRef out, GameState gameState) {
        for (int i = 1; i <= 6; i++) {
            BasicCommands.deleteCard(out, i);
        }
        for (int i = 0; i < gameState.player1.hand.size() && i < 6; i++) {
            BasicCommands.drawCard(out, gameState.player1.hand.get(i), i + 1, 0);
        }
    }



    private static Unit getHumanAvatar(GameState gameState) {
        if (gameState.humanAvatar != null) return gameState.humanAvatar;

        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit u = gameState.getUnitAt(x, y);
                if (u != null && u.getId() == 1) {
                    gameState.humanAvatar = u;
                    return u;
                }
            }
        }
        return null;
    }

    private static Unit getEnemyAvatar(GameState gameState, int owner) {
        if (owner == 1) {
            if (gameState.aiAvatar != null) return gameState.aiAvatar;
            for (int x = 1; x <= 9; x++) {
                for (int y = 1; y <= 5; y++) {
                    Unit u = gameState.getUnitAt(x, y);
                    if (u != null && u.getId() == 2) {
                        gameState.aiAvatar = u;
                        return u;
                    }
                }
            }
        } else {
            if (gameState.humanAvatar != null) return gameState.humanAvatar;
        }
        return null;
    }

    private static void summonRandomAdjacentWraithling(ActorRef out, GameState gameState, Unit center, int owner) {
        List<int[]> empty = getEmptyAdjacent(center, gameState);
        if (empty.isEmpty()) return;

        int[] pos = empty.get(RNG.nextInt(empty.size()));
        summonWraithlingAt(out, gameState, pos[0], pos[1], owner);
    }

    private static void summonWraithlingAt(ActorRef out, GameState gameState, int x, int y, int owner) {
        Unit wraithling = HumanTokenFactory.createWraithling(owner);
        gameState.placeUnit(wraithling, x, y);
        BasicCommands.drawUnit(out, wraithling, gameState.getTile(x, y));
        sleep(100);
        BasicCommands.setUnitHealth(out, wraithling, wraithling.getHealth());
        BasicCommands.setUnitAttack(out, wraithling, wraithling.getAttack());
        UNIT_CARD_NAMES.put(wraithling.getId(), "Wraithling");
    }

    private static List<int[]> getEmptyAdjacent(Unit center, GameState gameState) {
        List<int[]> result = new ArrayList<>();
        int cx = center.getPosition().getTilex();
        int cy = center.getPosition().getTiley();

        for (int x = cx - 1; x <= cx + 1; x++) {
            for (int y = cy - 1; y <= cy + 1; y++) {
                if (!gameState.isInBounds(x, y)) continue;
                if (x == cx && y == cy) continue;
                if (!gameState.hasUnitAt(x, y)) {
                    result.add(new int[]{x, y});
                }
            }
        }
        return result;
    }

    private static List<Unit> getAllBoardUnits(GameState gameState) {
        List<Unit> units = new ArrayList<>();
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit u = gameState.getUnitAt(x, y);
                if (u != null) units.add(u);
            }
        }
        return units;
    }

    private static void buffAttack(ActorRef out, Unit unit, int amount) {
        unit.setAttack(unit.getAttack() + amount);
        unit.setAttackPower(unit.getAttackPower() + amount);
        BasicCommands.setUnitAttack(out, unit, unit.getAttack());
    }

    private static void updateAvatarUi(ActorRef out, GameState gameState, Unit avatar) {
        if (avatar.getId() == 1) {
            gameState.player1.setHealth(avatar.getHealth());
            BasicCommands.setPlayer1Health(out, gameState.player1);
        } else if (avatar.getId() == 2) {
            gameState.player2.setHealth(avatar.getHealth());
            BasicCommands.setPlayer2Health(out, gameState.player2);
        }
    }

    private static void clearHighlights(ActorRef out, GameState gameState) {
        for (String key : new ArrayList<>(gameState.highlightedTiles)) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            BasicCommands.drawTile(out, gameState.getTile(x, y), 0);
        }
        gameState.clearHighlights();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {}
    }

    public static void resetState(){
        UNIT_CARD_NAMES.clear();
        humanHornRobustness = 0;
    }
}