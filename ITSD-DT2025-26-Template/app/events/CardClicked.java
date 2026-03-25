package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import java.util.ArrayList;

public class CardClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        if (!gameState.gameInitalised) return;

        int handPosition = message.get("position").asInt();

        if (gameState.selectedCardPosition == handPosition) {
            clearHighlights(out, gameState);
            gameState.selectedCardPosition = -1;
            return;
        }

        if (gameState.selectedUnit != null) {
            clearHighlights(out, gameState);
            gameState.selectedUnit = null;
        }

        gameState.selectedCardPosition = handPosition;
        clearHighlights(out, gameState);

        Card clickedCard = gameState.player1.getCardByHandPosition(handPosition);
        if (clickedCard == null) return;
        if (logic.HumanCardLogic.handleCardClicked(out, gameState, handPosition, clickedCard)) {
            return;
        }

        String unitConf = logic.CardFactory.getUnitConfigByCardName(clickedCard.getCardname());
        boolean isSpell = (unitConf == null);

        if (isSpell) {
            String spellName = clickedCard.getCardname();

            for (int x = 1; x <= 9; x++) {
                for (int y = 1; y <= 5; y++) {
                    Unit target = gameState.getUnitAt(x, y);
                    if (target != null && target.getOwner() == 2) {
                        if (spellName.contains("True Strike") || spellName.contains("Truestrike")){//true strike can hit any enemy
                            gameState.addHighlight(x, y);//true strike wasn't triggering correctly, added "Truestrike" check too
                            BasicCommands.drawTile(out, gameState.getTile(x, y), 2);
                        }else if (spellName.contains("Beam Shock")){
                            if(target.getId()!= 2){ //beam shock cannot hit the avatar
                                gameState.addHighlight(x, y);
                                BasicCommands.drawTile(out, gameState.getTile(x, y), 2);
                            }
                        }
                    }
                }
            }
            return;
        }
        highlightFriendlySummonTiles(out, gameState);
    }

    //highlight empty tile sadjacent to anyfriendly units
    private void highlightFriendlySummonTiles(ActorRef out, GameState gameState){
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 5; y++) {
                Unit u = gameState.getUnitAt(x, y);
                if (u != null && u.getOwner() == 1) {
                    for (int nx = x - 1; nx <= x + 1; nx++) {
                        for (int ny = y - 1; ny <= y + 1; ny++) {
                            if (nx == x && ny == y) continue; // skip self
                            if (gameState.isInBounds(nx, ny)
                                    && !gameState.hasUnitAt(nx, ny)
                                    && !gameState.isHighlighted(nx, ny)) {
                                gameState.addHighlight(nx, ny);
                                BasicCommands.drawTile(out, gameState.getTile(nx, ny), 1);
                            }
                        }
                    }
                }
            }
        }
    }

//		Unit avatar = gameState.humanAvatar;
//		if (avatar == null) {
//			for (int x = 1; x <= 9; x++) {
//				for (int y = 1; y <= 5; y++) {
//					Unit u = gameState.getUnitAt(x, y);
//					if (u != null && u.getId() == 1) {
//						avatar = u;
//						gameState.humanAvatar = u;
//					}
//				}
//			}
//		}
//
//		if (avatar == null) return;
//
//		int avatarX = avatar.getPosition().getTilex();
//		int avatarY = avatar.getPosition().getTiley();
//
//		for (int x = avatarX - 1; x <= avatarX + 1; x++) {
//			for (int y = avatarY - 1; y <= avatarY + 1; y++) {
//				if (gameState.isInBounds(x, y) && !gameState.hasUnitAt(x, y)) {
//					gameState.addHighlight(x, y);
//					BasicCommands.drawTile(out, gameState.getTile(x, y), 1);
//				}
//			}
//		}
//	}

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