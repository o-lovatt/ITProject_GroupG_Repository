package logic;

import structures.basic.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the set of cards currently held by a player.
 *
 * Rules to enforce:
 *  - Maximum hand size is 6 cards.
 *  - If a card is drawn when the hand is already full, the drawn card is
 *    discarded (overdraw) and the hand is left unchanged.
 *  - Cards are removed from the hand when played or cast.
 *
 * This class does NOT call any UI commands itself — callers are responsible
 * for updating the front-end after modifying the hand.
 */
public class Hand {

    /** Maximum number of cards a player may hold at one time. */
    public static final int MAX_HAND_SIZE = 6;

    /** The ordered list of cards currently in the hand (index 0 = leftmost). */
    private final List<Card> cards;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public Hand() {
        // TODO: initialise the cards list
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    /**
     * Attempts to add a card to the hand.
     *
     * Steps:
     *  1. Check the card is not null (throw IllegalArgumentException if so).
     *  2. Check if the hand is full — if so, log an overdraw message and return false.
     *  3. Otherwise add the card and return true.
     *
     * @param card the card to add.
     * @return true if added, false if overdraw occurred.
     */
    public boolean addCard(Card card) {
        // TODO: implement overdraw logic
        return false;
    }

    /**
     * Removes and returns the card at the given 0-based index.
     * Used when a card is played or cast.
     *
     * @param index 0-based position in the hand.
     * @return the removed Card.
     * @throws IndexOutOfBoundsException if the index is invalid.
     */
    public Card removeCardAt(int index) {
        // TODO: validate index, remove and return the card
        return null;
    }

    /**
     * Removes a specific card from the hand by reference.
     *
     * @param card the card to remove.
     * @return true if found and removed, false otherwise.
     */
    public boolean removeCard(Card card) {
        // TODO: remove by reference and return result
        return false;
    }

    // -----------------------------------------------------------------------
    // Queries / accessors
    // -----------------------------------------------------------------------

    /**
     * Returns the card at the given position without removing it.
     *
     * @param index 0-based position in the hand.
     * @return the Card at that position.
     */
    public Card getCard(int index) {
        // TODO: validate index and return the card
        return null;
    }

    /**
     * Returns an unmodifiable view of all cards currently in the hand.
     * Useful for rendering without allowing direct mutation.
     */
    public List<Card> getCards() {
        // TODO: return an unmodifiable list
        return null;
    }

    /** @return the number of cards currently in the hand. */
    public int getSize() {
        // TODO
        return 0;
    }

    /** @return true if the hand is at maximum capacity (6 cards). */
    public boolean isFull() {
        // TODO
        return false;
    }

    /** @return true if the hand contains no cards. */
    public boolean isEmpty() {
        // TODO
        return false;
    }

    /**
     * Returns the 1-based hand position of a given card.
     * Used to map a UI card click back to its hand slot.
     *
     * @param card the card to locate.
     * @return 1-based index, or -1 if not found.
     */
    public int getHandPosition(Card card) {
        // TODO: find the card, return index + 1 (or -1 if not found)
        return -1;
    }

    @Override
    public String toString() {
        // TODO: return a readable summary of the hand contents
        return "Hand[]";
    }
}
