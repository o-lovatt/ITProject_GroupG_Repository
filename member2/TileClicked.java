package events;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Handles tile clicks for simple board interaction:
 * 1) click a unit to highlight adjacent empty tiles
 * 2) click a highlighted tile to move the selected unit there
 * 3) click anything else on the board to clear highlights
 */
public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		if (!gameState.gameInitalised) {
			return;
		}

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		if (!gameState.isInBounds(tilex, tiley)) {
			return;
		}

		// Ignore click spam while a move animation is already in progress.
		if (gameState.unitMoving) {
			return;
		}

		// If a highlighted tile is clicked, move the selected unit.
		if (gameState.selectedUnit != null && gameState.isHighlighted(tilex, tiley) && !gameState.hasUnitAt(tilex, tiley)) {
			clearHighlights(out, gameState);

			Tile targetTile = gameState.getTile(tilex, tiley);
			Unit selectedUnit = gameState.selectedUnit;

			gameState.unitMoving = true;
			gameState.moveUnit(selectedUnit, tilex, tiley);
			BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);

			gameState.selectedUnit = null;
			gameState.unitMoving = false;
			return;
		}

		Unit clickedUnit = gameState.getUnitAt(tilex, tiley);

		// Clicking a unit selects it and highlights legal adjacent empty tiles.
		if (clickedUnit != null) {
			clearHighlights(out, gameState);
			gameState.selectedUnit = clickedUnit;
			highlightAdjacentEmptyTiles(out, gameState, tilex, tiley);
			return;
		}

		// Clicking an empty non-highlighted tile just clears current selection.
		clearHighlights(out, gameState);
		gameState.selectedUnit = null;
	}

	private void highlightAdjacentEmptyTiles(ActorRef out, GameState gameState, int x, int y) {
		List<int[]> adjacentTiles = new ArrayList<int[]>();
		adjacentTiles.add(new int[] { x + 1, y });
		adjacentTiles.add(new int[] { x - 1, y });
		adjacentTiles.add(new int[] { x, y + 1 });
		adjacentTiles.add(new int[] { x, y - 1 });

		for (int[] pos : adjacentTiles) {
			int tx = pos[0];
			int ty = pos[1];

			if (gameState.isInBounds(tx, ty) && !gameState.hasUnitAt(tx, ty)) {
				gameState.addHighlight(tx, ty);
				BasicCommands.drawTile(out, gameState.getTile(tx, ty), 1);
			}
		}
	}

	private void clearHighlights(ActorRef out, GameState gameState) {
		for (String key : new ArrayList<String>(gameState.highlightedTiles)) {
			String[] parts = key.split(",");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			BasicCommands.drawTile(out, gameState.getTile(x, y), 0);
		}
		gameState.clearHighlights();
	}
}
