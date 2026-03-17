package structures.basic;

import java.util.ArrayList;
import java.util.List;
import structures.basic.Card;
/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	int health;
	int mana;
	
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	public Card getCardByHandPosition(int position) {
		int index = position - 1;
		if (index >= 0 && index < hand.size()) {
			return hand.get(index);
		}
		return null;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	public List<Card> deck = new ArrayList<>();
	public List<Card> hand = new ArrayList<>();

	public int maxMana = 2;
	
}
