package logic;

import structures.basic.PlayerSide;

/**
 * Manages the turn flow, mana, and player switching in the game.
 * This class is responsible for the core turn-based mechanics.
 */
public class TurnManager {
    private int currentTurn;
    private PlayerSide activePlayer;
    private int player1Mana;
    private int player2Mana;

    /**
     * Initializes the TurnManager with the starting state.
     * The game starts with the human player (HUMAN_LEFT) on turn 1 with 1 mana.
     */
    public TurnManager() {
        this.currentTurn = 1;
        this.activePlayer = PlayerSide.HUMAN_LEFT;
        this.player1Mana = 1;
        this.player2Mana = 1;
    }

    /**
     * Adds mana to the active player based on the current turn.
     * The mana is capped at 9.
     */
    public void addMana() {
        if (activePlayer == PlayerSide.HUMAN_LEFT) {
            player1Mana = Math.min(currentTurn + 1, 9);
        } else {
            player2Mana = Math.min(currentTurn + 1, 9);
        }
    }

    /**
     * Drains all mana from the currently active player.
     */
    public void drainMana() {
        if (activePlayer == PlayerSide.HUMAN_LEFT) {
            player1Mana = 0;
        } else {
            player2Mana = 0;
        }
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
        drainMana();
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
        return side == PlayerSide.HUMAN_LEFT ? player1Mana : player2Mana;
    }
}