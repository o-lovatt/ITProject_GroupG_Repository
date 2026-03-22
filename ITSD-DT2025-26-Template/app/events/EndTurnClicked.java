package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.PlayerSide;
import logic.TurnManager;

public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (!gameState.gameInitalised) return;

		if (gameState.getTurnManager().getActivePlayer() != PlayerSide.HUMAN_LEFT || gameState.unitMoving) {
			return;
		}

		gameState.selectedUnit = null;
		gameState.selectedCardPosition = -1;

		gameState.getTurnManager().endTurn();


//		gameState.player2.setMana(gameState.player2.maxMana);
//		BasicCommands.setPlayer1Mana(out, gameState.player1);
//		BasicCommands.setPlayer2Mana(out, gameState.player2);


		logic.HandService.drawCard(out, gameState.player2, false);
		logic.AIController.playAITurn(out, gameState);
	}
}