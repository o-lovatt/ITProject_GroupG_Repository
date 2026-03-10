package structures;
import logic.PlayerState;
import logic.TurnManager;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
/**
 * This class can be used to hold information about the on-going game.
 * It's created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
    public boolean gameInitalised = false;

    public boolean something = false;

//}//remove this bracket later


    //private Board board; now redundant
    private PlayerState humanState;
    private PlayerState aiState;
    private PlayerSide activePlayer; //current players turn
    private int turnNumber;
    private GamePhase currentPhase; //START_TURN, MAIN_GAME, END_TURN
    private TurnManager turnManager;
    private PlayerSide winner;// null until gameOver = true
    private boolean gameOver;


    /// from zilu
    // 1-based board indexing: valid x = 1..9, valid y = 1..5
    public Tile[][] boardTiles = new Tile[10][6];
    public Unit[][] boardUnits = new Unit[10][6];

    public Player player1 = new Player(20, 0);
    public Player player2 = new Player(20, 0);

    public Unit humanAvatar = null;
    public Unit aiAvatar = null;

    public Unit selectedUnit = null;
    public Set<String> highlightedTiles = new HashSet<String>();

    public boolean unitMoving = false;

    public GameState() {//TEMPORARY ONLY!
        this.humanState = humanState;
        this.aiState = aiState;
        this.activePlayer = PlayerSide.HUMAN_LEFT;
        this.turnNumber = 1; //or 0 ??
        this.currentPhase = GamePhase.START_TURN;
        this.gameOver = false;
        this.winner = null;
    }
            //OLD REDUNDANT NOW
//        //board stuff
//        public Board getBoard() {
//            return board; //return game board
//        }
//
//        public Unit getUnit(Position pos){
//            return board.getUnit(pos); //return unit at it's position
//        }
//
//        public boolean isTileEmpty(Position pos){
//            return board.isTileEmpty(pos); //true if tile is empty
//        }
//
//        public Boolean isInBounds(Position pos){
//            return board.isInBounds(pos); //true if pos is in 9x5 board
//        }
//
//        public List<Unit> getUnitsFor(PlayerSide side){
//            return board.getUnitsOwnedBy(side);//return all units on players side
//        }
//
//        public List<Unit> getAllUnits(){
//            return board.getAllUnits();//return all units on the board
//        }
//
//        public List<Position> getValidSummonTiles(PlayerSide side) {
//            return board.getValidSummonTiles(side);//return valid tile positions for summoning units
//        }

        /// board methods - from zilu

        private String tileKey(int x, int y) {
            return x + "," + y;
        }

        public boolean isInBounds(int x, int y) {
            return x >= 1 && x <= 9 && y >= 1 && y <= 5;
        }

        public Tile getTile(int x, int y) {
            if (!isInBounds(x, y)) {
                return null;
            }
            return boardTiles[x][y];
        }

        public Unit getUnitAt(int x, int y) {
            if (!isInBounds(x, y)) {
                return null;
            }
            return boardUnits[x][y];
        }

        public boolean hasUnitAt(int x, int y) {
            return getUnitAt(x, y) != null;
        }

        public boolean isHighlighted(int x, int y) {
            return highlightedTiles.contains(tileKey(x, y));
        }

        public void addHighlight(int x, int y) {
            highlightedTiles.add(tileKey(x, y));
        }

        public void clearHighlights() {
            highlightedTiles.clear();
        }

        public void setTile(int x, int y, Tile tile) {
            if (isInBounds(x, y)) {
                boardTiles[x][y] = tile;
            }
        }

        public void placeUnit(Unit unit, int x, int y) {
            if (unit == null || !isInBounds(x, y)) {
                return;
            }
            boardUnits[x][y] = unit;
            unit.setPositionByTile(boardTiles[x][y]);
        }

        public void removeUnit(int x, int y) {
            if (!isInBounds(x, y)) {
                return;
            }
            boardUnits[x][y] = null;
        }

        public void moveUnit(Unit unit, int toX, int toY) {
            if (unit == null || !isInBounds(toX, toY)) {
                return;
            }

            int fromX = unit.getPosition().getTilex();
            int fromY = unit.getPosition().getTiley();

            if (isInBounds(fromX, fromY)) {
                boardUnits[fromX][fromY] = null;
            }

            boardUnits[toX][toY] = unit;
            unit.setPositionByTile(boardTiles[toX][toY]);
        }


        //player stuff
        public PlayerState getPlayerState(PlayerSide side) {
            if (side == PlayerSide.HUMAN_LEFT) {
                return humanState;
            } else {
                return aiState;//return playerstate on its own side
            }
        }

        public PlayerState getActivePlayerState() {
            return getPlayerState(activePlayer);//return current active player
        }

        public PlayerState getInactivePlayerState() {
            if (activePlayer == PlayerSide.HUMAN_LEFT){
                return aiState;
            }else{
                return humanState;
            }//return PLayerState(??);//// come back to here!!
        }


        public PlayerState getHumanState() {
            return humanState; //these two might not be needed but added just incase
        }

        public PlayerState getAiState(){
            return aiState; // :P
        }

        public boolean isActivePlayer(PlayerSide side){
            return activePlayer == side; //return if current side also current player?
        }

                    //(player state setters)
        public void setHumanState(PlayerState humanState){
            this.humanState = humanState;
        }
        public void setAiState(PlayerState aiState){
            this.aiState = aiState;
        }



        //turn stuff
        public PlayerSide getActivePlayer(){
            return activePlayer;
            }
        public int getTurnNumber(){
            return turnNumber;
            }

        public GamePhase getCurrentPhase(){
            return currentPhase;
            }

        public void setGamePhase(GamePhase phase){
            this.currentPhase = phase;
        }//advance the game phase

        public TurnManager getTurnManager(){
            return turnManager;// added after TurnManager was finished
        }
        public void setTurnManager(TurnManager tm){
            this.turnManager = tm;
        }


        //game over stuff
        public boolean isGameOver(){
            return gameOver;
            }

        public PlayerSide getWinner(){
                return winner;
            }

        public void checkWinner(){
            if (humanState.isDefeated()){
                setGameOver(PlayerSide.AI_RIGHT);
            } else if (aiState.isDefeated()) {
                setGameOver(PlayerSide.HUMAN_LEFT);
            }//checks if either player has been defeated
        }//sets game over and the winner

        public void advanceTurn(){
            if (activePlayer == PlayerSide.HUMAN_LEFT) {
                this.activePlayer = PlayerSide.AI_RIGHT;
            }else{
                this.activePlayer = PlayerSide.HUMAN_LEFT;
            }
            this.turnNumber++;
        }//switches player and increments turn


        public void setGameOver(PlayerSide winner){
            this.gameOver = true;
            this.winner = winner; //fix (this line was missing earlier oops)
            System.out.println("Game over! Winner: " + winner);
        }//set game to game over and prints winner




    //debugging return string
        @Override
        public String toString() {
            return "GameState: " + "Turn NUmber: " + turnNumber + "Active Player: " + activePlayer + "Game Phase: " + currentPhase
                    + "Human HP: " + humanState.getHealth()
                    + "Human Mana: " + humanState.getMana()
                    + "AI HP: " + aiState.getHealth()
                    + "AI Mana: " + aiState.getMana()
                    + gameOver + winner;
        }
    }




