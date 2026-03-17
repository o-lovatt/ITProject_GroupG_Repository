package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import structures.basic.Card;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class CardFactory {

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
}