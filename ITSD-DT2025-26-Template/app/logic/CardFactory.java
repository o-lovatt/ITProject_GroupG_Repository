package logic;

import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.MiniCard;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class responsible for constructing Card objects and building full
 * player decks from data templates.
 *
 * Each deck contains 20 cards: 2 copies of each of the 10 defined card types.
 * Card stats and names are taken directly from the Y2526 Decks specification.
 *
 * Usage:
 *   List<Card> humanCards = CardFactory.buildHumanDeck();
 *   Deck humanDeck = new Deck(humanCards);
 *
 *   List<Card> aiCards = CardFactory.buildAiDeck();
 *   Deck aiDeck = new Deck(aiCards);
 *
 * Unit config paths (unitConfig field on creature cards) should be updated
 * to match the actual StaticConfFiles constants once those are confirmed.
 *
 * Note: MiniCard and BigCard are loaded via the UI layer (BasicObjectBuilders).
 * This factory sets the card's core data fields; visual card assets are loaded
 * separately by the rendering system. Pass null for miniCard/bigCard here and
 * populate them when rendering to the hand.
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
     * Contains 2 copies of each of the 10 defined cards.
     *
     * @return a list of 20 {@link Card} objects ready to be passed to {@link Deck}.
     */
    public static List<Card> buildHumanDeck() {
        List<Card> deck = new ArrayList<>();
        int id = HUMAN_CARD_ID_START;

        // 2 copies of each card
        for (int copy = 0; copy < 2; copy++) {

            // ---- Creatures ----

            // Bad Omen: 0 cost, 0/1, Deathwatch: +1 attack permanently
            deck.add(createCreatureCard(id++, "Bad Omen", 0,
                    "conf/gameconfs/units/badomen.json"));     // TODO: confirm path

            // Gloom Chaser: 2 cost, 3/1, Opening Gambit: summon Wraithling behind
            deck.add(createCreatureCard(id++, "Gloom Chaser", 2,
                    "conf/gameconfs/units/gloomchaser.json")); // TODO: confirm path

            // Rock Pulveriser: 2 cost, 1/4, Provoke
            deck.add(createCreatureCard(id++, "Rock Pulveriser", 2,
                    "conf/gameconfs/units/rockpulveriser.json")); // TODO: confirm path

            // Shadow Watcher: 3 cost, 3/2, Deathwatch: +1/+1 permanently
            deck.add(createCreatureCard(id++, "Shadow Watcher", 3,
                    "conf/gameconfs/units/shadowwatcher.json")); // TODO: confirm path

            // Nightsorrow Assassin: 3 cost, 4/2, Opening Gambit: destroy adjacent enemy below max HP
            deck.add(createCreatureCard(id++, "Nightsorrow Assassin", 3,
                    "conf/gameconfs/units/nightsorrowassassin.json")); // TODO: confirm path

            // Bloodmoon Priestess: 4 cost, 3/3, Deathwatch: summon Wraithling on adjacent tile
            deck.add(createCreatureCard(id++, "Bloodmoon Priestess", 4,
                    "conf/gameconfs/units/bloodmoonpriestess.json")); // TODO: confirm path

            // Shadowdancer: 5 cost, 5/4, Deathwatch: deal 1 dmg to enemy avatar, heal self 1
            deck.add(createCreatureCard(id++, "Shadowdancer", 5,
                    "conf/gameconfs/units/shadowdancer.json")); // TODO: confirm path

            // ---- Spells ----

            // Horn of the Forsaken: 1 cost, Artifact 3 + On Hit: summon Wraithling
            deck.add(createSpellCard(id++, "Horn of the Forsaken", 1));

            // Wraithling Swarm: 3 cost, summon 3 Wraithlings in sequence
            deck.add(createSpellCard(id++, "Wraithling Swarm", 3));

            // Dark Terminus: 4 cost, destroy enemy creature + summon Wraithling on its tile
            deck.add(createSpellCard(id++, "Dark Terminus", 4));
        }

        System.out.println("[CardFactory] Human deck built: " + deck.size() + " cards.");
        return deck;
    }

    /**
     * Builds the full 20-card AI (Lyonar Generalist) deck.
     * Contains 2 copies of each of the 10 defined cards.
     *
     * @return a list of 20 {@link Card} objects ready to be passed to {@link Deck}.
     */
    public static List<Card> buildAiDeck() {
        List<Card> deck = new ArrayList<>();
        int id = AI_CARD_ID_START;

        for (int copy = 0; copy < 2; copy++) {

            // ---- Creatures ----

            // Swamp Entangler: 1 cost, 0/3, Provoke
            deck.add(createCreatureCard(id++, "Swamp Entangler", 1,
                    "conf/gameconfs/units/swampentangler.json")); // TODO: confirm path

            // Silverguard Squire: 1 cost, 1/1, Opening Gambit: +1/+1 to adjacent ally in front/behind avatar
            deck.add(createCreatureCard(id++, "Silverguard Squire", 1,
                    "conf/gameconfs/units/silverguardsquire.json")); // TODO: confirm path

            // Skyrock Golem: 2 cost, 4/2, no abilities
            deck.add(createCreatureCard(id++, "Skyrock Golem", 2,
                    "conf/gameconfs/units/skyrockgolem.json")); // TODO: confirm path

            // Saberspine Tiger: 3 cost, 3/2, Rush
            deck.add(createCreatureCard(id++, "Saberspine Tiger", 3,
                    "conf/gameconfs/units/saberspine.json")); // TODO: confirm path

            // Silverguard Knight: 3 cost, 1/5, Zeal + Provoke
            deck.add(createCreatureCard(id++, "Silverguard Knight", 3,
                    "conf/gameconfs/units/silverguardknight.json")); // TODO: confirm path

            // Young Flamewing: 4 cost, 5/4, Flying
            deck.add(createCreatureCard(id++, "Young Flamewing", 4,
                    "conf/gameconfs/units/youngflamewing.json")); // TODO: confirm path

            // Ironcliffe Guardian: 5 cost, 3/10, Provoke
            deck.add(createCreatureCard(id++, "Ironcliffe Guardian", 5,
                    "conf/gameconfs/units/ironcliffeguardian.json")); // TODO: confirm path

            // ---- Spells ----

            // Sundrop Elixir: 1 cost, heal allied unit for 4 (does not increase max HP)
            deck.add(createSpellCard(id++, "Sundrop Elixir", 1));

            // True Strike: 1 cost, deal 2 damage to an enemy unit
            deck.add(createSpellCard(id++, "True Strike", 1));

            // Beam Shock: 0 cost, stun target non-avatar enemy unit for 1 turn
            deck.add(createSpellCard(id++, "Beam Shock", 0));
        }

        System.out.println("[CardFactory] AI deck built: " + deck.size() + " cards.");
        return deck;
    }

    // -----------------------------------------------------------------------
    // Card construction helpers
    // -----------------------------------------------------------------------

    /**
     * Creates a creature card (summons a unit when played).
     *
     * @param id         unique card ID.
     * @param name       display name of the card.
     * @param manaCost   mana required to play this card.
     * @param unitConfig path to the unit's JSON config file (from StaticConfFiles).
     * @return a configured {@link Card} marked as a creature.
     */
    public static Card createCreatureCard(int id, String name, int manaCost, String unitConfig) {
        // MiniCard and BigCard visuals are loaded at render time by BasicObjectBuilders.
        // Passing null here is intentional — the UI layer populates these.
        Card card = new Card(id, name, manaCost, null, null, true, unitConfig);
        return card;
    }

    /**
     * Creates a spell card (applies an effect when played, does not summon a unit).
     *
     * @param id       unique card ID.
     * @param name     display name of the card.
     * @param manaCost mana required to play this card.
     * @return a configured {@link Card} marked as a spell (isCreature = false).
     */
    public static Card createSpellCard(int id, String name, int manaCost) {
        Card card = new Card(id, name, manaCost, null, null, false, null);
        return card;
    }

    /**
     * Creates a token card (e.g. Wraithling). Tokens are not placed in decks
     * but may be created during gameplay by card effects.
     *
     * @param id         unique card ID.
     * @param name       token name.
     * @param unitConfig path to the token unit's JSON config file.
     * @return a configured token {@link Card}.
     */
    public static Card createTokenCard(int id, String name, String unitConfig) {
        return new Card(id, name, 0, null, null, true, unitConfig);
    }
}
