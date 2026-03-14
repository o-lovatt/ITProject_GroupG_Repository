package logic;

import structures.basic.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class responsible for constructing Card objects and building full
 * player decks from the Y2526 card specification.
 *
 * Each deck contains 20 cards: 2 copies of each of the 10 defined card types.
 *
 * Usage:
 *   List<Card> humanCards = CardFactory.buildHumanDeck();
 *   Deck humanDeck = new Deck(humanCards);
 *
 *   List<Card> aiCards = CardFactory.buildAiDeck();
 *   Deck aiDeck = new Deck(aiCards);
 *
 * Note: MiniCard and BigCard visuals are loaded at render time by BasicObjectBuilders.
 * Pass null for those fields here — the UI layer populates them separately.
 *
 * The unitConfig paths marked TODO should be replaced with the correct
 * constants from StaticConfFiles once those are confirmed.
 */
public class CardFactory {

    // -----------------------------------------------------------------------
    // Card ID ranges (avoids collisions with unit IDs used elsewhere)
    // -----------------------------------------------------------------------

    /** Starting card ID for human (Abyssian) deck cards. */
    private static final int HUMAN_CARD_ID_START = 1000;

    /** Starting card ID for AI (Lyonar) deck cards. */
    private static final int AI_CARD_ID_START = 2000;

    // -----------------------------------------------------------------------
    // Public deck builders
    // -----------------------------------------------------------------------

    /**
     * Builds the full 20-card Human (Abyssian Swarm) deck.
     * 2 copies of each of the 10 cards defined in the Y2526 spec.
     *
     * Human cards (creatures):
     *  - Bad Omen         (0 mana, 0/1, Deathwatch)
     *  - Gloom Chaser     (2 mana, 3/1, Opening Gambit)
     *  - Rock Pulveriser  (2 mana, 1/4, Provoke)
     *  - Shadow Watcher   (3 mana, 3/2, Deathwatch)
     *  - Nightsorrow Assassin (3 mana, 4/2, Opening Gambit)
     *  - Bloodmoon Priestess  (4 mana, 3/3, Deathwatch)
     *  - Shadowdancer      (5 mana, 5/4, Deathwatch)
     *
     * Human cards (spells):
     *  - Horn of the Forsaken (1 mana, Artifact + On Hit)
     *  - Wraithling Swarm     (3 mana, summon 3 Wraithlings)
     *  - Dark Terminus        (4 mana, destroy enemy + summon Wraithling)
     *
     * Steps:
     *  1. Create an empty list.
     *  2. Loop twice (for 2 copies).
     *  3. Inside the loop, call createCreatureCard() or createSpellCard() for each card.
     *  4. Add each result to the list.
     *  5. Return the completed list.
     *
     * @return a list of 20 Card objects.
     */
    public static List<Card> buildHumanDeck() {
        List<Card> deck = new ArrayList<>();
        int id = HUMAN_CARD_ID_START;

        // TODO: Add 2 copies of each of the 10 human cards using the helpers below.
        // Example (uncomment and fill in the unitConfig path):
        // for (int copy = 0; copy < 2; copy++) {
        //     deck.add(createCreatureCard(id++, "Bad Omen", 0, "TODO: path"));
        //     deck.add(createCreatureCard(id++, "Gloom Chaser", 2, "TODO: path"));
        //     ... and so on for all 10 cards
        // }

        System.out.println("[CardFactory] Human deck built: " + deck.size() + " cards.");
        return deck;
    }

    /**
     * Builds the full 20-card AI (Lyonar Generalist) deck.
     * 2 copies of each of the 10 cards defined in the Y2526 spec.
     *
     * AI cards (creatures):
     *  - Swamp Entangler      (1 mana, 0/3, Provoke)
     *  - Silverguard Squire   (1 mana, 1/1, Opening Gambit)
     *  - Skyrock Golem        (2 mana, 4/2)
     *  - Saberspine Tiger     (3 mana, 3/2, Rush)
     *  - Silverguard Knight   (3 mana, 1/5, Zeal + Provoke)
     *  - Young Flamewing      (4 mana, 5/4, Flying)
     *  - Ironcliffe Guardian  (5 mana, 3/10, Provoke)
     *
     * AI cards (spells):
     *  - Sundrop Elixir  (1 mana, heal ally 4)
     *  - True Strike     (1 mana, deal 2 damage to enemy)
     *  - Beam Shock      (0 mana, stun non-avatar enemy for 1 turn)
     *
     * @return a list of 20 Card objects.
     */
    public static List<Card> buildAiDeck() {
        List<Card> deck = new ArrayList<>();
        int id = AI_CARD_ID_START;

        // TODO: Add 2 copies of each of the 10 AI cards using the helpers below.

        System.out.println("[CardFactory] AI deck built: " + deck.size() + " cards.");
        return deck;
    }

    // -----------------------------------------------------------------------
    // Card construction helpers
    // -----------------------------------------------------------------------

    /**
     * Creates a creature card (summons a unit when played).
     *
     * Hint: use the Card(int id, String cardname, int manacost,
     *                     MiniCard miniCard, BigCard bigCard,
     *                     boolean isCreature, String unitConfig) constructor.
     * Pass null for miniCard and bigCard — the UI populates these at render time.
     * Set isCreature = true.
     *
     * @param id         unique card ID.
     * @param name       display name of the card.
     * @param manaCost   mana required to play this card.
     * @param unitConfig path to the unit's JSON config file.
     * @return a configured Card marked as a creature.
     */
    public static Card createCreatureCard(int id, String name, int manaCost, String unitConfig) {
        // TODO: construct and return a Card with isCreature = true
        return null;
    }

    /**
     * Creates a spell card (applies an effect, does not summon a unit).
     *
     * Hint: same as createCreatureCard but isCreature = false and unitConfig = null.
     *
     * @param id       unique card ID.
     * @param name     display name of the card.
     * @param manaCost mana required to play this card.
     * @return a configured Card marked as a spell.
     */
    public static Card createSpellCard(int id, String name, int manaCost) {
        // TODO: construct and return a Card with isCreature = false
        return null;
    }

    /**
     * Creates a token card (e.g. a Wraithling).
     * Tokens are not placed in decks but are created during gameplay by card effects.
     * Tokens always cost 0 mana.
     *
     * @param id         unique card ID.
     * @param name       token name.
     * @param unitConfig path to the token unit's JSON config file.
     * @return a configured token Card.
     */
    public static Card createTokenCard(int id, String name, String unitConfig) {
        // TODO: construct and return a token Card (cost 0, isCreature = true)
        return null;
    }
}
