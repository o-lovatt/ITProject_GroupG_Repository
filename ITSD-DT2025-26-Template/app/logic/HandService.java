package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.Player;

public class HandService {

    public static void drawCard(ActorRef out, Player player, boolean isHuman) {

        if (player.deck.isEmpty() || player.hand.size() >= 6) {
            System.out.println("can't draw card here");
            return;
        }

        Card drawnCard = player.deck.remove(0);

        player.hand.add(drawnCard);

        int handPosition = player.hand.size();

        if (isHuman) {
            System.out.println("player got " + drawnCard.getCardname());
            BasicCommands.drawCard(out, drawnCard, handPosition, 0);

            try { Thread.sleep(300); } catch (Exception e) {}
        } else {
            System.out.println("AI get: " + drawnCard.getCardname() + " get into AI hand");
        }
    }
}