package logic;

import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.MiniCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections; 
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Factory class responsible for constructing Card objects and building full
 * player decks from data templates.
 */
public class CardFactory {

    // -----------------------------------------------------------------------
    // Card ID ranges (avoids collisions with unit IDs used elsewhere)
    // -----------------------------------------------------------------------

    private static final int HUMAN_CARD_ID_START = 1000;
    private static final int AI_CARD_ID_START = 2000;
    private static int cardIdCounter = 100; 

    public static List<Card> createLyonarDeck() {
        List<Card> deck = new ArrayList<>();

        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_true_strike, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_sundrop_elixir, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_beam_shock, cardIdCounter++, Card.class));

        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_swamp_entangler, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_squire, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_skyrock_golem, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_saberspine_tiger, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_young_flamewing, cardIdCounter++, Card.class));
        deck.add(BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliffe_guardian, cardIdCounter++, Card.class));

        Collections.shuffle(deck); 
        return deck;
    }

    public static String getUnitConfigByCardName(String cardName) {
        if (cardName == null) return null;

        if (cardName.contains("Golem")) return "conf/gameconfs/units/skyrock_golem.json";
        if (cardName.contains("Entangler")) return "conf/gameconfs/units/swamp_entangler.json";
        if (cardName.contains("Knight")) return "conf/gameconfs/units/silverguard_knight.json";
        if (cardName.contains("Tiger")) return "conf/gameconfs/units/saberspine_tiger.json";
        if (cardName.contains("Flamewing")) return "conf/gameconfs/units/young_flamewing.json";
        if (cardName.contains("Squire")) return "conf/gameconfs/units/silverguard_squire.json";
        if (cardName.contains("Guardian") || cardName.contains("Ironcliff")) return "conf/gameconfs/units/ironcliff_guardian.json";

        return null;
    }

    public static List<Card> buildHumanDeck() {
        List<Card> deck = new ArrayList<>();
        int id = HUMAN_CARD_ID_START;
        for (int copy = 0; copy < 2; copy++) {
            deck.add(createCreatureCard(id++, "Bad Omen", 0, "conf/gameconfs/units/badomen.json"));
            deck.add(createCreatureCard(id++, "Gloom Chaser", 2, "conf/gameconfs/units/gloomchaser.json"));
            deck.add(createCreatureCard(id++, "Rock Pulveriser", 2, "conf/gameconfs/units/rockpulveriser.json"));
            deck.add(createCreatureCard(id++, "Shadow Watcher", 3, "conf/gameconfs/units/shadowwatcher.json"));
            deck.add(createCreatureCard(id++, "Nightsorrow Assassin", 3, "conf/gameconfs/units/nightsorrowassassin.json"));
            deck.add(createCreatureCard(id++, "Bloodmoon Priestess", 4, "conf/gameconfs/units/bloodmoonpriestess.json"));
            deck.add(createCreatureCard(id++, "Shadowdancer", 5, "conf/gameconfs/units/shadowdancer.json"));
            deck.add(createSpellCard(id++, "Horn of the Forsaken", 1));
            deck.add(createSpellCard(id++, "Wraithling Swarm", 3));
            deck.add(createSpellCard(id++, "Dark Terminus", 4));
        }
        Collections.shuffle(deck);
        System.out.println("[CardFactory] Human deck built: " + deck.size() + " cards.");
        return deck;
    }

    public static List<Card> buildAiDeck() {
        List<Card> deck = new ArrayList<>();
        int id = AI_CARD_ID_START;
        for (int copy = 0; copy < 2; copy++) {
            deck.add(createCreatureCard(id++, "Swamp Entangler", 1, "conf/gameconfs/units/swampentangler.json"));
            deck.add(createCreatureCard(id++, "Silverguard Squire", 1, "conf/gameconfs/units/silverguardsquire.json"));
            deck.add(createCreatureCard(id++, "Skyrock Golem", 2, "conf/gameconfs/units/skyrockgolem.json"));
            deck.add(createCreatureCard(id++, "Saberspine Tiger", 3, "conf/gameconfs/units/saberspine.json"));
            deck.add(createCreatureCard(id++, "Silverguard Knight", 3, "conf/gameconfs/units/silverguardknight.json"));
            deck.add(createCreatureCard(id++, "Young Flamewing", 4, "conf/gameconfs/units/youngflamewing.json"));
            deck.add(createCreatureCard(id++, "Ironcliffe Guardian", 5, "conf/gameconfs/units/ironcliffeguardian.json"));
            deck.add(createSpellCard(id++, "Sundrop Elixir", 1));
            deck.add(createSpellCard(id++, "True Strike", 1));
            deck.add(createSpellCard(id++, "Beam Shock", 0));
        }
        Collections.shuffle(deck); 
        System.out.println("[CardFactory] AI deck built: " + deck.size() + " cards.");
        return deck;
    }

    public static Card createCreatureCard(int id, String name, int manaCost, String unitConfig) {
        Card card = new Card(id, name, manaCost, null, null, true, unitConfig);
        return card;
    }

    public static Card createSpellCard(int id, String name, int manaCost) {
        Card card = new Card(id, name, manaCost, null, null, false, null);
        return card;
    }

    public static Card createTokenCard(int id, String name, String unitConfig) {
        return new Card(id, name, 0, null, null, true, unitConfig);
    }
}
