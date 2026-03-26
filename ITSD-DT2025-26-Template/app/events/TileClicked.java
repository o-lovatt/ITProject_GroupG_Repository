package events;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.Card;

public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        //System.out.println("DEBUG TileClicked ENTERED");
        if (!gameState.gameInitalised) {
            //System.out.println("DEBUG early return: not initialised");
            return;
        }
        if (gameState.isGameOver()) {
            //System.out.println("DEBUG early return: game over");
            return;
        }
        if (gameState.getTurnManager() == null) {
            //System.out.println("DEBUG early return: turnManager is NULL");
            return;
        }
        if (gameState.getTurnManager().getActivePlayer() != structures.PlayerSide.HUMAN_LEFT) {
//            System.out.println("DEBUG early return: wrong player = "
//                    + gameState.getTurnManager().getActivePlayer());
            return;
        }
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

//        System.out.println("DEBUG TileClicked: gameOver=" + gameState.isGameOver()
//                + " activePlayer=" + gameState.getTurnManager().getActivePlayer());/// debug
//        System.out.println("DEBUG: selectedCard=" + gameState.selectedCardPosition
//                + " selectedUnit=" + (gameState.selectedUnit != null ? gameState.selectedUnit.getId() : "null")
//                + " unitMoving=" + gameState.unitMoving
//                + " isHighlighted=" + gameState.isHighlighted(tilex, tiley));
//        /// DEBUG!

		if (!gameState.isInBounds(tilex, tiley)) return;

		if (gameState.unitMoving) return;

		if (logic.HumanCardLogic.handleTileClicked(out, gameState, tilex, tiley)) {
    		return;
		}

		if (gameState.selectedCardPosition > 0 && gameState.isHighlighted(tilex, tiley)) {
			Card cardToPlay = gameState.player1.getCardByHandPosition(gameState.selectedCardPosition);
			if (cardToPlay == null) { gameState.selectedCardPosition = -1; clearHighlights(out, gameState); return; }

			if (gameState.player1.getMana() < cardToPlay.getManacost()) {
				BasicCommands.addPlayer1Notification(out, "Not enough Mana!", 2);
				gameState.selectedCardPosition = -1; clearHighlights(out, gameState); return;
			}

			clearHighlights(out, gameState);

			gameState.player1.setMana(gameState.player1.getMana() - cardToPlay.getManacost());
			BasicCommands.setPlayer1Mana(out, gameState.player1);
			gameState.player1.hand.remove(cardToPlay);

			for (int i = 1; i <= 6; i++) BasicCommands.deleteCard(out, i);
			for (int i = 0; i < gameState.player1.hand.size(); i++) {
				BasicCommands.drawCard(out, gameState.player1.hand.get(i), i + 1, 0);
			}

			String unitConf = logic.CardFactory.getUnitConfigByCardName(cardToPlay.getCardname());
			if (unitConf != null) {
                if(gameState.hasUnitAt(tilex, tiley)){ //second guard to stop overlapping tile summning
                    gameState.selectedCardPosition = -1;
                    clearHighlights(out, gameState);
                    BasicCommands.addPlayer1Notification(out, "Tile is occupied", 2);
                    return;
                }
				Tile targetTile = gameState.getTile(tilex, tiley);
				BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_summon), targetTile);

				Unit summonedUnit = utils.BasicObjectBuilders.loadUnit(unitConf, cardToPlay.getId(), Unit.class);
				summonedUnit.setPositionByTile(targetTile);
				summonedUnit.setOwner(1);

				if (cardToPlay.getBigCard() != null) {
					summonedUnit.setHealth(cardToPlay.getBigCard().getHealth());
					summonedUnit.setAttack(cardToPlay.getBigCard().getAttack());
                    summonedUnit.setAttack(cardToPlay.getBigCard().getAttack());
                    summonedUnit.setAttackPower(cardToPlay.getBigCard().getAttack());
				}
                applyUnitKeywords(summonedUnit, cardToPlay.getCardname());

                //rush can still attack!
                summonedUnit.setHasMoved(true);
                if(!summonedUnit.hasRush()){
                    summonedUnit.setHasAttacked(true);
                }

				gameState.placeUnit(summonedUnit, tilex, tiley);
				BasicCommands.drawUnit(out, summonedUnit, targetTile);
				try { Thread.sleep(100); } catch (Exception e) {}
				BasicCommands.setUnitHealth(out, summonedUnit, summonedUnit.getHealth());
				BasicCommands.setUnitAttack(out, summonedUnit, summonedUnit.getAttack());

				summonedUnit.setHasMoved(true);
				summonedUnit.setHasAttacked(true);
			} else {
				Unit targetUnit = gameState.getUnitAt(tilex, tiley);
				if (targetUnit != null) {
					executeSpell(out, gameState, cardToPlay, targetUnit, gameState.getTile(tilex, tiley));
				}
			}

			gameState.selectedCardPosition = -1;
			return;
		}

		if (gameState.selectedUnit != null && gameState.isHighlighted(tilex, tiley)) {
			Unit selectedUnit = gameState.selectedUnit;
			Unit targetUnit = gameState.getUnitAt(tilex, tiley);

			if (targetUnit == null) {
				gameState.unitMoving = true;
				clearHighlights(out, gameState);
				BasicCommands.moveUnitToTile(out, selectedUnit, gameState.getTile(tilex, tiley));
				gameState.moveUnit(selectedUnit, tilex, tiley);
				selectedUnit.setHasMoved(true);
				//gameState.unitMoving = false; removed to stop animation bug
			} else if (targetUnit.getOwner() != selectedUnit.getOwner()) {
				clearHighlights(out, gameState);
				new logic.CombatResolver().executeAttack(out, gameState, selectedUnit, targetUnit);
				selectedUnit.setHasMoved(true);
				selectedUnit.setHasAttacked(true);
			}
			gameState.selectedUnit = null;
			return;
		}

		Unit clickedUnit = gameState.getUnitAt(tilex, tiley);
		if (clickedUnit != null) {
			clearHighlights(out, gameState);

			if (clickedUnit.getOwner() != 1) {
				gameState.selectedUnit = null;
				return;
			}
//            System.out.println("DEBUG unit selected: id=" + clickedUnit.getId()
//                    + " hasMoved=" + clickedUnit.hasMoved()
//                    + " hasAttacked=" + clickedUnit.hasAttacked()
//                    + " owner=" + clickedUnit.getOwner());
            /// DEBUG

            //vvv new, added to check if stunned
            if(clickedUnit.isStunned()){
                BasicCommands.addPlayer1Notification(out, "Unit is stunned", 2);
                gameState.selectedUnit = null;
                return;
            }

			gameState.selectedUnit = clickedUnit;

			if (!clickedUnit.hasMoved()) {
				List<Tile> reachableTiles = logic.MovementLogic.getReachableTiles(gameState, clickedUnit);
				for (Tile t : reachableTiles) {
					gameState.addHighlight(t.getTilex(), t.getTiley());
					BasicCommands.drawTile(out, t, 1);
				}
			}

			if (!clickedUnit.hasAttacked()) {
				int ux = clickedUnit.getPosition().getTilex();
				int uy = clickedUnit.getPosition().getTiley();

                boolean isProvoked = logic.MovementLogic.isProvoked(gameState, clickedUnit);

				for (int x = ux - 1; x <= ux + 1; x++) {
					for (int y = uy - 1; y <= uy + 1; y++) {
						if (gameState.isInBounds(x, y)) {
							Unit target = gameState.getUnitAt(x, y);
							if (target != null && target.getOwner() != clickedUnit.getOwner()) {
                                if(isProvoked && !target.hasProvoke())//this was missing, if provoked, only highlight provoking units
                                    continue;
								gameState.addHighlight(x, y);
								BasicCommands.drawTile(out, gameState.getTile(x, y), 2);
							}
						}
					}
				}
			}
			return;
		}

		clearHighlights(out, gameState);
		gameState.selectedUnit = null;
		gameState.selectedCardPosition = -1;
	}

	private void executeSpell(ActorRef out, GameState gameState, Card card, Unit target, Tile tile) {
		String spellName = card.getCardname();
		if(spellName.contains("True Strike") || spellName.contains("Truestrike")) {
            logic.SpellService.castTrueStrike(out, gameState, target);
        }else if(spellName.contains("Beam Shock")){
            logic.SpellService.castBeamShock(out, gameState, target);
        }
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

    private void applyUnitKeywords(Unit unit, String cardName){
        if(cardName == null)
            return;
        switch(cardName){
            case "Swamp Entangler":
                unit.setProvoke(true);//this was missing
                break;
            case "Silverguard Knight":
                unit.setProvoke(true);
                unit.setZeal(true);
                break;
            case "Ironcliffe Guardian":
                unit.setProvoke(true);
                break;
            case "Young Flamewing":
                unit.setFlying(true);
                break;
            case "Sabrespine Tiger":
                unit.setRush(true);
                break;
        }
    }
}