package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.PlayerSide;
import logic.TurnManager;
import structures.basic.Unit;

public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (!gameState.gameInitalised) return;
        if(gameState.isGameOver()) return;// turns kept incrementing after game over, fixed

		if (gameState.getTurnManager().getActivePlayer() != PlayerSide.HUMAN_LEFT || gameState.unitMoving) {
			return;
		}

		gameState.selectedUnit = null;
		gameState.selectedCardPosition = -1;

		gameState.getTurnManager().endTurn();

        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit u = gameState.getUnitAt(x, y);
                if (u != null) {
                    u.clearStun();
                }//added to clear stun at the end of the humans turn
            }
        }


//		gameState.player2.setMana(gameState.player2.maxMana);
//		BasicCommands.setPlayer1Mana(out, gameState.player1);
//		BasicCommands.setPlayer2Mana(out, gameState.player2);


		logic.HandService.drawCard(out, gameState.player2, false);
		logic.AIController.playAITurn(out, gameState);
	}
}