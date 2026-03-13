package events;

import commands.BasicCommands;
import structures.GameState;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
//import demo.CommandDemo;
import structures.PlayerSide;
//import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import structures.basic.Tile;
import structures.basic.Unit;
import logic.PlayerState;
import logic.TurnManager;



/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

    /// from zilu
    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        if (gameState.gameInitalised) {
            return;
        }

        gameState.gameInitalised = true;
        gameState.something = true;

        // Draw the full 9x5 board and keep the tile objects in GameState.
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                gameState.setTile(x, y, tile);
                BasicCommands.drawTile(out, tile, 0);
            }
        }

        // Create both avatars and place them on mirrored start positions.
        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 100, Unit.class);
        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 200, Unit.class);

        gameState.humanAvatar = humanAvatar;
        gameState.aiAvatar = aiAvatar;

        // Start positions used here: player 1 = [2,3], player 2 mirrored = [8,3]
        gameState.placeUnit(humanAvatar, 2, 3);
        gameState.placeUnit(aiAvatar, 8, 3);

        BasicCommands.drawUnit(out, humanAvatar, gameState.getTile(2, 3));
        BasicCommands.drawUnit(out, aiAvatar, gameState.getTile(8, 3));

        // Display avatar combat stats (basic starting values).
        BasicCommands.setUnitAttack(out, humanAvatar, 2);
        BasicCommands.setUnitHealth(out, humanAvatar, 20);
        BasicCommands.setUnitAttack(out, aiAvatar, 2);
        BasicCommands.setUnitHealth(out, aiAvatar, 20);

        //attach playerstates to gamestate
        /// TODO REPLACE NUL WITH CARD DECK WHEN THIS IXISTS
        PlayerState humanState = new PlayerState(PlayerSide.HUMAN_LEFT, humanAvatar);
        PlayerState aiState = new PlayerState(PlayerSide.AI_RIGHT, aiAvatar);


        gameState.setHumanState(humanState);
        gameState.setAiState(aiState);

        gameState.setTurnManager(new TurnManager(gameState));
        gameState.getTurnManager().startTurn();

        //attach turnmanager to gamestate
        gameState.player1.setMana(humanState.getMana());
        gameState.player2.setMana(aiState.getMana());

        // Draw player health/mana in the UI.
        BasicCommands.setPlayer1Health(out, gameState.player1);
        BasicCommands.setPlayer2Health(out, gameState.player2);
        BasicCommands.setPlayer1Mana(out, gameState.player1);
        BasicCommands.setPlayer2Mana(out, gameState.player2);

        BasicCommands.addPlayer1Notification(out, "Game initialised", 2);
    }
}



