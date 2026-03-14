package logic;

import structures.basic.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player's deck of cards — the shuffled draw pile.
 *
 * Rules to enforce:
 *  - Each deck has 20 cards (2 copies of each of the 10 card types).
 *  - The deck is shuffled at the start of the game.
 *  - At game start, 3 cards are drawn into the hand.
 *  - At the start of each subsequent turn, 1 card is drawn.
 *  - If the deck is empty when a draw is attempted, nothing happens (no crash).
 *
 * This class does NOT call UI commands — callers are responsible for
 * updating the front-end after draw operations.
 */
public class Deck {

    /** Total number of cards in a standard starting deck. */
    public static final int DECK_SIZE = 20;

    /** Number of cards drawn into the hand at the very start of the game. */
    public static final int INITIAL_DRAW_COUNT = 3;

    /** The ordered draw pile; index 0 is the "top" of the deck. */
    private final List<Card> drawPile;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Constructs a Deck from an ordered list of cards and shuffles it immediately.
     *
     * Steps:
     *  1. Validate that the list is not null or empty (throw IllegalArgumentException if so).
     *  2. Copy the list into drawPile.
     *  3. Call shuffle().
     *
     * @param cards the full list of cards to build the deck from.
     */
    public Deck(List<Card> cards) {
        // TODO: validate, copy into drawPile, then shuffle
        this.drawPile = new ArrayList<>();
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    /**
     * Shuffles the draw pile into a random order.
     * Hint: Collections.shuffle() does exactly this.
     */
    public void shuffle() {
        // TODO: shuffle drawPile
    }

    /**
     * Draws the top card from the deck and returns it.
     * If the deck is empty, log a message and return null.
     *
     * Hint: remove index 0 from the drawPile list.
     *
     * @return the drawn Card, or null if the deck is empty.
     */
    public Card drawCard() {
        // TODO: check empty, then remove and return top card
        return null;
    }

    /**
     * Draws the initial 3 cards at game start and adds each to the provided hand.
     * The hand's addCard() method handles overdraw automatically.
     *
     * Steps:
     *  1. Loop INITIAL_DRAW_COUNT times.
     *  2. Each iteration: call drawCard(), then hand.addCard() if the result is not null.
     *
     * @param hand the player's Hand to deal cards into.
     */
    public void drawInitialHand(Hand hand) {
        // TODO: draw INITIAL_DRAW_COUNT cards into the hand
    }

    /**
     * Draws 1 card at the start of a turn and attempts to add it to the hand.
     * Returns false if the deck was empty or overdraw occurred.
     *
     * @param hand the player's Hand to deal the card into.
     * @return true if the card was successfully added, false otherwise.
     */
    public boolean drawEndOfTurn(Hand hand) {
        // TODO: draw one card and add it to the hand; return the result
        return false;
    }

    // -----------------------------------------------------------------------
    // Queries / accessors
    // -----------------------------------------------------------------------

    /** @return the number of cards remaining in the draw pile. */
    public int getRemainingCount() {
        // TODO
        return 0;
    }

    /** @return true if there are no cards left to draw. */
    public boolean isEmpty() {
        // TODO
        return false;
    }

    /**
     * Returns an unmodifiable view of the draw pile.
     * Primarily for debugging — the UI only shows the count, not the contents.
     */
    public List<Card> getDrawPile() {
        // TODO: return unmodifiable list
        return null;
    }

    @Override
    public String toString() {
        // TODO: return a readable summary e.g. "Deck[remaining=17/20]"
        return "Deck[]";
    }
}
