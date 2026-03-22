package logic;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Unit;

public class CardPlayValidator {
    //check player has sufficient mana to play card
    public static boolean sufficientManaCheck(Player player, Card card){
        if(player == null || card == null)
            return false;
        return player.getMana() >= card.getManacost();//returns true if they have enough mana
    }
    //check if tile is valid for summoning
    public static boolean isValidSummonTarget(GameState gameState, int tilex, int tiley) {
        if (!gameState.isInBounds(tilex, tiley))
            return false; //checks tile is in bounds
        if (gameState.hasUnitAt(tilex, tiley))
            return false; //checks if there's already a unit on the tile
        if (!gameState.isHighlighted(tilex, tiley))
            return false; //checks if the tile is not highlhghted
        return true;
    }


    //spell targeting checks
    public static boolean isValidSpellTarget(Card card, GameState gameState, int tilex, int tiley){
        //returns true if tile is valuid for a spell
        if(card == null || !gameState.isInBounds(tilex, tiley))
            return false;
        if(!gameState.isHighlighted(tilex, tiley))
            return false;

        Unit target = gameState.getUnitAt(tilex, tiley);
        if(target == null)
            return false;

        String name = card.getCardname();
        if(name == null)
            return false;

        switch(name){
            case "True Strike":
            case "Beam Shock":
                return target.getOwner() == 2;
            case  "Sundrop Elixir":
                return true;
            default:
                return false; //if the spell is unknow just reject it
        }
    }

    //check playing a create card is fully valid, sufficient mana and valid tile
    public static boolean isCreaturePlayValid(Player player, Card card, GameState gameState, int tilex, int tiley) {
        return sufficientManaCheck(player, card) && isValidSummonTarget(gameState, tilex, tiley);
    }
    //check playing a spell card is fully valid, same checks as above
    public static boolean isSpellPlayvalid(Player player, Card card, GameState gameState, int tilex, int tiley){
        return(sufficientManaCheck(player, card) && isValidSpellTarget(card, gameState, tilex, tiley));
    }
}
