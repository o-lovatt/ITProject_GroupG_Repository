package logic;

import structures.PlayerSide;
import structures.GameState;

/**
 * Manages the turn flow, mana, and player switching in the game.
 * This class is responsible for the core turn-based mechanics.
 */
public class TurnManager {
    private int currentTurn;
    private PlayerSide activePlayer;
    private GameState gameState;

    /**
     * Initializes the TurnManager with the starting state.
     * The game starts with the human player (HUMAN_LEFT) on turn 1 with 1 mana.
     */
    public TurnManager(GameState gameState) {
        this.gameState = gameState;
        this.currentTurn = 1;
        this.activePlayer = PlayerSide.HUMAN_LEFT;
        this.gameState.getHumanState().setManaForTurn(currentTurn);
        this.gameState.getAiState().setManaForTurn(currentTurn); //minor name changes to align with new methods
    }

    public void startTurn(){
        addMana();
        //action flags for deck/hand are reset here when they exist
    }

    /**
     * Adds mana to the active player based on the current turn.
     * The mana is capped at 9.
     */
    public void addMana() {
        if (activePlayer == PlayerSide.HUMAN_LEFT) {
            gameState.getHumanState().setManaForTurn(Math.min(currentTurn + 1, 9));
        } else {
            gameState.getAiState().setManaForTurn(Math.min(currentTurn + 1, 9));
        }
    }

    /**
     * Drains all mana from the currently active player.
     */
    public static void drainMana(GameState gameState) {
        gameState.getActivePlayerState().resetMana();
    }

    /**
     * Switches the active player to the opposite side.
     */
    public void switchActivePlayer() {
        activePlayer = activePlayer.opposite();
    }

    /**
     * Ends the current turn and progresses the game.
     * This method will drain mana, switch player, increment turn, and add new mana.
     */
    public void endTurn() {
        drainMana(gameState);
        switchActivePlayer();
        currentTurn++;
        addMana();
    }

    /**
     * Gets the current turn number.
     * @return The current turn.
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Gets the currently active player.
     * @return The active PlayerSide.
     */
    public PlayerSide getActivePlayer() {
        return activePlayer;
    }

    /**
     * Gets the current mana of the specified player.
     * @param side The PlayerSide to check.
     * @return The current mana value.
     */
    public int getPlayerMana(PlayerSide side) {
        return side == PlayerSide.HUMAN_LEFT
                ? gameState.getHumanState().getMana()
                : gameState.getAiState().getMana();
    }
}