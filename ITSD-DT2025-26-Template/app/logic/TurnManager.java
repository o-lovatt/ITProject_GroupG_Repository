package logic;

import structures.PlayerSide;
import structures.GameState;
import structures.basic.Unit;

public class TurnManager {
    private int currentTurn;
    private PlayerSide activePlayer;
    private GameState gameState;

    public TurnManager(GameState gameState) {
        this.gameState = gameState;
        this.currentTurn = 1;
        this.activePlayer = PlayerSide.HUMAN_LEFT;
        this.gameState.getHumanState().setManaForTurn(currentTurn);
        this.gameState.getAiState().setManaForTurn(currentTurn);
    }

    public void startTurn() {
        addMana();
    }

    public void addMana() {
        if (activePlayer == PlayerSide.HUMAN_LEFT) {
            gameState.getHumanState().setManaForTurn(Math.min(currentTurn + 1, 9));
        } else {
            gameState.getAiState().setManaForTurn(Math.min(currentTurn + 1, 9));
        }
    }

    public static void drainMana(GameState gameState) {
        gameState.getActivePlayerState().resetMana();
    }

    public void switchActivePlayer() {
        activePlayer = activePlayer.opposite();
    }

    public void endTurn() {
        drainMana(gameState);
        switchActivePlayer();
        currentTurn++;
        addMana();
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public PlayerSide getActivePlayer() {
        return activePlayer;
    }

    public int getPlayerMana(PlayerSide side) {
        return side == PlayerSide.HUMAN_LEFT
                ? gameState.getHumanState().getMana()
                : gameState.getAiState().getMana();
    }
}