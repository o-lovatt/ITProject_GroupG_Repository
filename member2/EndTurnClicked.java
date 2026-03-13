package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Minimal end-turn behaviour for Member 2:
 * just clear any board selection/highlight state.
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		for (String key : new ArrayList<String>(gameState.highlightedTiles)) {
			String[] parts = key.split(",");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			BasicCommands.drawTile(out, gameState.getTile(x, y), 0);
		}
		gameState.clearHighlights();
		gameState.selectedUnit = null;
	}
}
