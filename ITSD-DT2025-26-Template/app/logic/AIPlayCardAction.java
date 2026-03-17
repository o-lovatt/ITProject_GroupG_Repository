package logic;

import structures.basic.Card;
import structures.basic.Tile;

public class AIPlayCardAction {
    public Card card;
    public Tile targetTile;
    public int handPosition;

    public AIPlayCardAction(Card card, Tile targetTile, int handPosition) {
        this.card = card;
        this.targetTile = targetTile;
        this.handPosition = handPosition;
    }
}