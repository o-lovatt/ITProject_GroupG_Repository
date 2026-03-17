package logic;

import structures.basic.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the set of cards currently held by a player.
 *
 * Rules enforced:
 *  - Maximum hand size is 6 cards.
 *  - If a card is drawn when the hand is already full, the drawn card is
 *    discarded (overdraw) and the hand is left unchanged.
 *  - Cards are removed from the hand when played or cast.
 *
 * This class does NOT call any UI commands itself – callers are responsible
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
        this.cards = new ArrayList<>();
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    /**
     * Attempts to add a card to the hand.
     *
     * @param card the card to add; must not be null.
     * @return {@code true} if the card was added successfully,
     *         {@code false} if the hand was full (overdraw – card is discarded).
     */
    public boolean addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add a null card to the hand.");
        }
        if (isFull()) {
            // Overdraw: hand is full, discard the drawn card.
            System.out.println("[Hand] Overdraw! Hand is full (" + MAX_HAND_SIZE
                    + " cards). Card discarded: " + card.getCardname());
            return false;
        }
        cards.add(card);
        System.out.println("[Hand] Card added: " + card.getCardname()
                + " | Hand size: " + cards.size());
        return true;
    }

    /**
     * Removes and returns the card at the given hand position (0-based index).
     * Used when a card is played or cast.
     *
     * @param index 0-based position of the card in the hand.
     * @return the removed {@link Card}.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public Card removeCardAt(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid hand index: " + index + " (hand size: " + cards.size() + ")");
        }
        Card removed = cards.remove(index);
        System.out.println("[Hand] Card played/removed: " + removed.getCardname()
                + " | Hand size: " + cards.size());
        return removed;
    }

    /**
     * Removes a specific card from the hand by reference.
     * Used when a card is played or cast and the caller already holds a reference to it.
     *
     * @param card the card to remove.
     * @return {@code true} if the card was found and removed, {@code false} otherwise.
     */
    public boolean removeCard(Card card) {
        boolean removed = cards.remove(card);
        if (removed) {
            System.out.println("[Hand] Card removed: " + card.getCardname()
                    + " | Hand size: " + cards.size());
        } else {
            System.out.println("[Hand] Attempted to remove card not in hand: "
                    + card.getCardname());
        }
        return removed;
    }

    // -----------------------------------------------------------------------
    // Queries / accessors
    // -----------------------------------------------------------------------

    /**
     * Returns the card at the given hand position without removing it.
     *
     * @param index 0-based position of the card in the hand.
     * @return the {@link Card} at that position.
     */
    public Card getCard(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException(
                    "Invalid hand index: " + index + " (hand size: " + cards.size() + ")");
        }
        return cards.get(index);
    }

    /**
     * Returns an unmodifiable view of all cards currently in the hand.
     * Useful for rendering the hand in the UI without allowing direct mutation.
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    /** @return the number of cards currently in the hand. */
    public int getSize() {
        return cards.size();
    }

    /** @return {@code true} if the hand is at maximum capacity (6 cards). */
    public boolean isFull() {
        return cards.size() >= MAX_HAND_SIZE;
    }

    /** @return {@code true} if the hand contains no cards. */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Returns the 1-based hand position index of a given card.
     * Used to map a clicked card in the UI back to its hand slot.
     *
     * @param card the card to locate.
     * @return 1-based index, or -1 if the card is not in the hand.
     */
    public int getHandPosition(Card card) {
        int idx = cards.indexOf(card);
        return (idx == -1) ? -1 : idx + 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Hand[" + cards.size() + "/" + MAX_HAND_SIZE + "]: ");
        for (int i = 0; i < cards.size(); i++) {
            sb.append("[").append(i).append("] ")
              .append(cards.get(i).getCardname())
              .append("(").append(cards.get(i).getManacost()).append(") ");
        }
        return sb.toString().trim();
    }
}
