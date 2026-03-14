package events;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (!gameState.gameInitalised) return;

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		if (!gameState.isInBounds(tilex, tiley)) return;
		if (gameState.unitMoving) return;

		if (gameState.selectedUnit != null && gameState.isHighlighted(tilex, tiley)) {
			clearHighlights(out, gameState);

			Tile targetTile = gameState.getTile(tilex, tiley);
			Unit selectedUnit = gameState.selectedUnit;
			Unit targetUnit = gameState.getUnitAt(tilex, tiley);

			if (targetUnit == null) {
				gameState.unitMoving = true;

				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
				gameState.moveUnit(selectedUnit, tilex, tiley);

				selectedUnit.setHasMoved(true);
				gameState.selectedUnit = null;
				gameState.unitMoving = false;

			} else if (targetUnit.getId() != selectedUnit.getId()) {
				logic.CombatResolver combat = new logic.CombatResolver();
				combat.executeAttack(out, gameState, selectedUnit, targetUnit);
				gameState.selectedUnit = null;
			}
			return;
		}

		Unit clickedUnit = gameState.getUnitAt(tilex, tiley);
		if (clickedUnit != null) {
			clearHighlights(out, gameState);
			gameState.selectedUnit = clickedUnit;

			List<Tile> reachableTiles = logic.MovementLogic.getReachableTiles(gameState, clickedUnit);
			for (Tile t : reachableTiles) {
				gameState.addHighlight(t.getTilex(), t.getTiley());
				BasicCommands.drawTile(out, t, 1);
			}

			int ux = clickedUnit.getPosition().getTilex();
			int uy = clickedUnit.getPosition().getTiley();
			for (int x = ux - 1; x <= ux + 1; x++) {
				for (int y = uy - 1; y <= uy + 1; y++) {
					if (gameState.isInBounds(x, y)) {
						Unit target = gameState.getUnitAt(x, y);
						if (target != null && target.getId() != clickedUnit.getId()) {
							gameState.addHighlight(x, y);
							BasicCommands.drawTile(out, gameState.getTile(x, y), 2);
						}
					}
				}
			}
			return;
		}

		clearHighlights(out, gameState);
		gameState.selectedUnit = null;
	}

	private void clearHighlights(ActorRef out, GameState gameState) {
		for (String key : new ArrayList<>(gameState.highlightedTiles)) {
			String[] parts = key.split(",");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			BasicCommands.drawTile(out, gameState.getTile(x, y), 0);
		}
		gameState.clearHighlights();
	}
}