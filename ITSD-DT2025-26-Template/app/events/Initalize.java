package events;

import commands.BasicCommands;
import structures.GameState;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import structures.PlayerSide;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.basic.Tile;
import structures.basic.Unit;
import logic.PlayerState;
import logic.TurnManager;

public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        if (gameState.gameInitalised) {
            return;
        }

        gameState.gameInitalised = true;
        gameState.something = true;

        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                gameState.setTile(x, y, tile);
                BasicCommands.drawTile(out, tile, 0);
            }
        }

        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 1, Unit.class);
        humanAvatar.setMaxHealth(20);
        humanAvatar.heal(20);
        humanAvatar.setAttackPower(2);

        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 2, Unit.class);
        aiAvatar.setMaxHealth(20);
        aiAvatar.heal(20);
        aiAvatar.setAttackPower(2);

        gameState.humanAvatar = humanAvatar;
        gameState.aiAvatar = aiAvatar;

        gameState.placeUnit(humanAvatar, 2, 3);
        gameState.placeUnit(aiAvatar, 8, 3);

        BasicCommands.drawUnit(out, humanAvatar, gameState.getTile(2, 3));
        BasicCommands.drawUnit(out, aiAvatar, gameState.getTile(8, 3));

        PlayerState humanState = new PlayerState(PlayerSide.HUMAN_LEFT, humanAvatar);
        PlayerState aiState = new PlayerState(PlayerSide.AI_RIGHT, aiAvatar);

        gameState.setHumanState(humanState);
        gameState.setAiState(aiState);

        gameState.setTurnManager(new TurnManager(gameState));
        gameState.getTurnManager().startTurn();

        gameState.player1.setMana(humanState.getMana());
        gameState.player2.setMana(aiState.getMana());

        gameState.player1.setHealth(20);
        gameState.player2.setHealth(20);

        BasicCommands.setPlayer1Health(out, gameState.player1);
        BasicCommands.setPlayer2Health(out, gameState.player2);
        BasicCommands.setPlayer1Mana(out, gameState.player1);
        BasicCommands.setPlayer2Mana(out, gameState.player2);

        BasicCommands.addPlayer1Notification(out, "Game initialised", 2);

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BasicCommands.setUnitAttack(out, humanAvatar, humanAvatar.getAttackPower());
            BasicCommands.setUnitHealth(out, humanAvatar, humanAvatar.getHealth());
            BasicCommands.setUnitAttack(out, aiAvatar, aiAvatar.getAttackPower());
            BasicCommands.setUnitHealth(out, aiAvatar, aiAvatar.getHealth());
        }).start();
    }
}

