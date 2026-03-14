package logic;

import structures.basic.Tile;
import structures.basic.Unit;

public class AIMoveAction {
    public Unit unit;
    public Tile targetTile;

    public AIMoveAction(Unit unit, Tile targetTile) {
        this.unit = unit;
        this.targetTile = targetTile;
    }
}