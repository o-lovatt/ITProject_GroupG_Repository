package events;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;

public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		if (gameState.isGameOver()) {
			return;
		}
		if (!gameState.gameInitalised) return;

		if (gameState.getTurnManager().getActivePlayer() != structures.PlayerSide.HUMAN_LEFT) {
			return;
		}

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		if (!gameState.isInBounds(tilex, tiley)) return;

		if (gameState.unitMoving) return;

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
				Tile targetTile = gameState.getTile(tilex, tiley);
				BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_summon), targetTile);

				Unit summonedUnit = utils.BasicObjectBuilders.loadUnit(unitConf, cardToPlay.getId(), Unit.class);
				summonedUnit.setPositionByTile(targetTile);
				summonedUnit.setOwner(1);

				if (cardToPlay.getBigCard() != null) {
					summonedUnit.setHealth(cardToPlay.getBigCard().getHealth());
					summonedUnit.setAttack(cardToPlay.getBigCard().getAttack());
				}

				gameState.placeUnit(summonedUnit, tilex, tiley);
				BasicCommands.drawUnit(out, summonedUnit, targetTile);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				BasicCommands.setUnitHealth(out, summonedUnit, summonedUnit.getHealth());
				BasicCommands.setUnitAttack(out, summonedUnit, summonedUnit.getAttack());
				if (cardToPlay.getCardname().contains("Tiger")) {
					summonedUnit.setHasMoved(false);
					summonedUnit.setHasAttacked(false);
					BasicCommands.addPlayer1Notification(out, "RUSH!", 2);
				}
				else {
					summonedUnit.setHasMoved(true);
					summonedUnit.setHasAttacked(true);
				}
				String cardName = cardToPlay.getCardname();
				if (cardName.contains("Entangler") ||
						cardName.contains("Guardian") ||
						cardName.contains("Knight") ||
						cardName.contains("Pulveriser")) {

					summonedUnit.isProvoke = true;

					BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_buff), targetTile);
					BasicCommands.addPlayer1Notification(out, "PROVOKE!", 2);
				}
			}
			else {
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
				gameState.unitMoving = false;
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

			gameState.selectedUnit = clickedUnit;

			boolean isProvoked = false;
			List<Unit> adjacentProvokers = new ArrayList<>();
			int ux = clickedUnit.getPosition().getTilex();
			int uy = clickedUnit.getPosition().getTiley();

			for (int x = ux - 1; x <= ux + 1; x++) {
				for (int y = uy - 1; y <= uy + 1; y++) {
					if (gameState.isInBounds(x, y)) {
						Unit neighbor = gameState.getUnitAt(x, y);
						if (neighbor != null && neighbor.getOwner() != clickedUnit.getOwner() && neighbor.isProvoke) {
							isProvoked = true;
							adjacentProvokers.add(neighbor);
						}
					}
				}
			}

			if (isProvoked) {
				BasicCommands.addPlayer1Notification(out, "Provoked! Must attack!", 2);

				if (!clickedUnit.hasAttacked()) {
					for (Unit provoker : adjacentProvokers) {
						int px = provoker.getPosition().getTilex();
						int py = provoker.getPosition().getTiley();
						gameState.addHighlight(px, py);
						BasicCommands.drawTile(out, gameState.getTile(px, py), 2);
					}
				}
			} else {
				if (!clickedUnit.hasMoved()) {
					List<Tile> reachableTiles = logic.MovementLogic.getReachableTiles(gameState, clickedUnit);
					for (Tile t : reachableTiles) {
						gameState.addHighlight(t.getTilex(), t.getTiley());
						BasicCommands.drawTile(out, t, 1);
					}
				}

				if (!clickedUnit.hasAttacked()) {
					for (int x = ux - 1; x <= ux + 1; x++) {
						for (int y = uy - 1; y <= uy + 1; y++) {
							if (gameState.isInBounds(x, y)) {
								Unit target = gameState.getUnitAt(x, y);
								if (target != null && target.getOwner() != clickedUnit.getOwner()) {
									gameState.addHighlight(x, y);
									BasicCommands.drawTile(out, gameState.getTile(x, y), 2);
								}
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
		try {
			if (spellName.contains("True Strike")) {
				BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_inmolation), tile);
				Thread.sleep(400);

				target.setHealth(target.getHealth() - 2);
				BasicCommands.setUnitHealth(out, target, target.getHealth());
				System.out.println("DEBUG: True Strike hit " + target.getId() + " for 2 damage!");

				if (target.getHealth() <= 0) {
					BasicCommands.playUnitAnimation(out, target, structures.basic.UnitAnimationType.death);
					Thread.sleep(1000);
					BasicCommands.deleteUnit(out, target);
					gameState.removeUnit(tile.getTilex(), tile.getTiley());
				}
			}
			else if (spellName.contains("Sundrop Elixir")) {
				BasicCommands.playEffectAnimation(out, utils.BasicObjectBuilders.loadEffect(utils.StaticConfFiles.f1_buff), tile);
				Thread.sleep(400);
				target.setHealth(Math.min(target.getHealth() + 5, 20)); // 假设上限20
				BasicCommands.setUnitHealth(out, target, target.getHealth());
			}
		} catch (InterruptedException e) { e.printStackTrace(); }
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