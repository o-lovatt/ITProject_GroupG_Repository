package structures;

import java.util.HashSet;
import java.util.Set;

import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Holds the current game state for the template game.
 * This version adds the minimum board/unit/highlight state needed for Member 2.
 */
public class GameState {

	public boolean gameInitalised = false;
	public boolean something = false;

	// 1-based board indexing: valid x = 1..9, valid y = 1..5
	public Tile[][] boardTiles = new Tile[10][6];
	public Unit[][] boardUnits = new Unit[10][6];

	public Player player1 = new Player(20, 0);
	public Player player2 = new Player(20, 0);

	public Unit humanAvatar = null;
	public Unit aiAvatar = null;

	public Unit selectedUnit = null;
	public Set<String> highlightedTiles = new HashSet<String>();

	public boolean unitMoving = false;

	private String tileKey(int x, int y) {
		return x + "," + y;
	}

	public boolean isInBounds(int x, int y) {
		return x >= 1 && x <= 9 && y >= 1 && y <= 5;
	}

	public Tile getTile(int x, int y) {
		if (!isInBounds(x, y)) {
			return null;
		}
		return boardTiles[x][y];
	}

	public Unit getUnitAt(int x, int y) {
		if (!isInBounds(x, y)) {
			return null;
		}
		return boardUnits[x][y];
	}

	public boolean hasUnitAt(int x, int y) {
		return getUnitAt(x, y) != null;
	}

	public boolean isHighlighted(int x, int y) {
		return highlightedTiles.contains(tileKey(x, y));
	}

	public void addHighlight(int x, int y) {
		highlightedTiles.add(tileKey(x, y));
	}

	public void clearHighlights() {
		highlightedTiles.clear();
	}

	public void setTile(int x, int y, Tile tile) {
		if (isInBounds(x, y)) {
			boardTiles[x][y] = tile;
		}
	}

	public void placeUnit(Unit unit, int x, int y) {
		if (unit == null || !isInBounds(x, y)) {
			return;
		}
		boardUnits[x][y] = unit;
		unit.setPositionByTile(boardTiles[x][y]);
	}

	public void removeUnit(int x, int y) {
		if (!isInBounds(x, y)) {
			return;
		}
		boardUnits[x][y] = null;
	}

	public void moveUnit(Unit unit, int toX, int toY) {
		if (unit == null || !isInBounds(toX, toY)) {
			return;
		}

		int fromX = unit.getPosition().getTilex();
		int fromY = unit.getPosition().getTiley();

		if (isInBounds(fromX, fromY)) {
			boardUnits[fromX][fromY] = null;
		}

		boardUnits[toX][toY] = unit;
		unit.setPositionByTile(boardTiles[toX][toY]);
	}
}
