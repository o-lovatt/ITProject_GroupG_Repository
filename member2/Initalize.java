package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Triggered once when the browser-side game loop is ready.
 * This version replaces the demo with a real Week 1 / Member 2 board setup.
 */
public class Initalize implements EventProcessor {

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

		// Draw player health/mana in the UI.
		BasicCommands.setPlayer1Health(out, gameState.player1);
		BasicCommands.setPlayer2Health(out, gameState.player2);
		BasicCommands.setPlayer1Mana(out, gameState.player1);
		BasicCommands.setPlayer2Mana(out, gameState.player2);

		BasicCommands.addPlayer1Notification(out, "Game initialised", 2);
	}
}
