package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import commands.BasicCommands;

public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		System.out.println("User clicked tile at X: " + tilex + ", Y: " + tiley);

		// Create the tile the user clicked
		Tile clickedTile = new Tile();
		clickedTile.setTilex(tilex);
		clickedTile.setTiley(tiley);

		// TODO: Replace with real Board logic once your teammates build it!
		Unit unitOnTile = null;

		// 1. NOTHING IS SELECTED YET
		if (gameState.selectedUnit == null) {

			if (unitOnTile != null) {
				// Select the unit
				gameState.selectedUnit = unitOnTile;
				System.out.println("Selected Unit ID: " + unitOnTile.getId());

				// VISUALS: Highlight the tile so the player knows it is selected!
				BasicCommands.drawTile(out, clickedTile, 1);
			} else {
				System.out.println("Clicked an empty tile. Select a unit first!");
			}

		}
		// 2. A UNIT IS ALREADY SELECTED!
		else {
			if (unitOnTile == null) {
				// Try to move to the empty tile
				boolean success = gameState.actionService.performMove(gameState.selectedUnit, clickedTile);

				if (success) {
					// VISUALS: Tell the frontend to animate the walking!
					BasicCommands.moveUnitToTile(out, gameState.selectedUnit, clickedTile);

					// Turn off the highlight on the old tile (we will just reset the whole board later)
					gameState.selectedUnit = null;
				}
			} else if (unitOnTile.getId() != gameState.selectedUnit.getId()) {
				// Try to attack the enemy
				boolean success = gameState.actionService.performAttack(gameState.selectedUnit, unitOnTile);

				if (success) {
					System.out.println("Attack Successful!");
					gameState.selectedUnit = null;
				}
			} else {
				// Clicked the exact same unit again to Deselect
				gameState.selectedUnit = null;

				// VISUALS: Turn the highlight off
				BasicCommands.drawTile(out, clickedTile, 0);
				System.out.println("Unit deselected.");
			}
		}
	}
}