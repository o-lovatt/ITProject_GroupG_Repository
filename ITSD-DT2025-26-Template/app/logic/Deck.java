package logic;

import structures.basic.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player's deck of cards — the shuffled draw pile from which
 * cards are drawn into the player's hand.
 *
 * Rules enforced:
 *  - Each deck has 20 cards (2 copies of each of the 10 cards for this player).
 *  - The deck is shuffled at the start of the game.
 *  - At game start, 3 cards are drawn into the hand.
 *  - At the start of each subsequent turn, 1 card is drawn.
 *  - If the deck is empty, no card is drawn (no fatigue damage in this version).
 *
 * This class does NOT call UI commands — callers are responsible for
 * updating the front-end after draw operations.
 */
public class Deck {

    /** The total number of cards in a standard starting deck. */
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
     * The provided list should contain exactly {@value #DECK_SIZE} cards.
     *
     * @param cards the full list of cards to build the deck from.
     */
    public Deck(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("Deck cannot be built from a null or empty card list.");
        }
        this.drawPile = new ArrayList<>(cards);
        shuffle();
        System.out.println("[Deck] Deck created and shuffled. Size: " + drawPile.size());
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    /**
     * Shuffles the draw pile randomly.
     * Called automatically on construction; can also be called explicitly if needed.
     */
    public void shuffle() {
        Collections.shuffle(drawPile);
        System.out.println("[Deck] Deck shuffled.");
    }

    /**
     * Draws the top card from the deck and returns it.
     * If the deck is empty, logs a message and returns {@code null}.
     *
     * @return the drawn {@link Card}, or {@code null} if the deck is empty.
     */
    public Card drawCard() {
        if (isEmpty()) {
            System.out.println("[Deck] Draw attempted but deck is empty!");
            return null;
        }
        Card drawn = drawPile.remove(0);
        System.out.println("[Deck] Card drawn: " + drawn.getCardname()
                + " | Remaining: " + drawPile.size());
        return drawn;
    }

    /**
     * Draws the initial 3 cards at game start and attempts to add each to the
     * provided hand. Respects the hand's overdraw logic — if the hand fills
     * up during the draw, remaining cards are discarded.
     *
     * @param hand the player's {@link Hand} to deal cards into.
     */
    public void drawInitialHand(Hand hand) {
        System.out.println("[Deck] Drawing initial hand (" + INITIAL_DRAW_COUNT + " cards)...");
        for (int i = 0; i < INITIAL_DRAW_COUNT; i++) {
            Card card = drawCard();
            if (card != null) {
                hand.addCard(card); // Hand handles overdraw internally
            }
        }
        System.out.println("[Deck] Initial hand drawn. " + hand);
    }

    /**
     * Draws 1 card at the start of a turn and attempts to add it to the hand.
     * If the hand is full the drawn card is discarded (overdraw).
     *
     * @param hand the player's {@link Hand} to deal the card into.
     * @return {@code true} if the card was successfully added to the hand,
     *         {@code false} if the deck was empty or an overdraw occurred.
     */
    public boolean drawEndOfTurn(Hand hand) {
        Card card = drawCard();
        if (card == null) {
            return false; // Deck empty, nothing to draw.
        }
        return hand.addCard(card); // Returns false on overdraw.
    }

    // -----------------------------------------------------------------------
    // Queries / accessors
    // -----------------------------------------------------------------------

    /** @return the number of cards remaining in the draw pile. */
    public int getRemainingCount() {
        return drawPile.size();
    }

    /** @return {@code true} if there are no cards left to draw. */
    public boolean isEmpty() {
        return drawPile.isEmpty();
    }

    /**
     * Returns an unmodifiable view of the draw pile.
     * Primarily useful for debugging; the UI only shows the card count, not the pile contents.
     */
    public List<Card> getDrawPile() {
        return Collections.unmodifiableList(drawPile);
    }

    @Override
    public String toString() {
        return "Deck[remaining=" + drawPile.size() + "/" + DECK_SIZE + "]";
    }
}
