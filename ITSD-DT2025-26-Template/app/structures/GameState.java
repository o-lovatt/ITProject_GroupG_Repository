package structures;
import logic.PlayerState;

import java.util.List;
/**
 * This class can be used to hold information about the on-going game.
 * It's created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
//    public boolean gameInitalised = false;
//
//    public boolean something = false;
//}


    private final Board board;
    private final PlayerState humanState;
    private final PlayerState aiState;
    private PlayerSide activePlayer; //current players turn
    private int turnNumber;
    private GamePhase currentPhase; //START_TURN, MAIN_GAME, END_TURN
    private PlayerSide winner;// null until gameOver = true
    private boolean gameOver;


        public GameState(Board board, PlayerState humanState, PlayerState aiState){
            this.board = board;
            this.humanState = humanState;
            this.aiState = aiState;
            this.activePlayer = PlayerSide.HUMAN_LEFT;
            this.turnNumber = 1; //or 0 ??
            this.currentPhase = GamePhase.START_TURN;
            this.gameOver = false;
            this.winner = null;
        }

        //board stuff
        public Board getBoard() {
            return board; //return game board
        }

        public Unit getUnit(Position pos){
            return board.getUnit(pos); //return unit at it's position
        }

        public boolean isTileEmpty(Position pos){
            return board.isTileEmpty(pos); //true if tile is empty
        }

        public Boolean isInBounds(Position pos){
            return boan.isInBounds(pos); //true if pos is in 9x5 board
        }

        public List<Unit> getUnitsFor(PlayerSide side){
            return board.getUnitsOwnedBy(side);//return all units on players side
        }

        public List<Unit> getAllUnits(){
            return board.getAllUnits();//return all units on the board
        }

        public List<Position> getValidSummonTiles(PlayerSide) {
            return board.getValidSummonTiles(side);//return valid tile positions for summoning units
        }



        //player stuff
        public PlayerState getPlayerState(PlayerSide side) {
            if (side == PlayerSide.HUMAN_LEFT) {
                return humanState;
            } else {
                return aiState;//return playerstate on its own side
            }

        public PlayerState getActivePlayerState () {
            return getPlayerState(activePlayer);//return current active player
        }

        public PlayerState getInactivePlayerState () {
//                return PLayerState(??);
//            }

        }//// come back to here!!!


        public PlayerState getHumanState () {
            return humanState;
        }

        public PlayerState getAiState(){
            return aiState;
        }


        //turn stuff



    //debugging return string
        @Override
        public String toString(){
            return "GameState: "
                    + "Turn NUmber: " + turnNumber
                    + "Active Player: " + activePlayer
                    + "Game Phase: " + currentPhase
                    + "Human HP: " + humanState.getHealth()
                    + "Human Mana: " + humanState.getMana()
                    + "AI HP: " + aiState.getHealth()
                    + "AI Mana: " + aiState.getMana()
                    + gameOver + winner;
        }
    }

