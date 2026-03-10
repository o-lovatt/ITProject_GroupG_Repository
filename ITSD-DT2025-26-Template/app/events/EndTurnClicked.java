package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.PlayerSide;

/**
 * Minimal end-turn behaviour for Member 2:
 * just clear any board selection/highlight state.
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if(!gameState.gameInitalised)
            return;
        if(gameState.isGameOver())
            return;

        for (String key : new ArrayList<String>(gameState.highlightedTiles)) {
			String[] parts = key.split(",");
			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			BasicCommands.drawTile(out, gameState.getTile(x, y), 0);
		}
		gameState.clearHighlights();
		gameState.selectedUnit = null;


        //end turn and start next turn
        gameState.getTurnManager().endTurn();
        //update ui
        BasicCommands.setPlayer1Mana(out, gameState.player1);
        BasicCommands.setPlayer2Mana(out, gameState.player2);
	}
}
