package events;

import commands.BasicCommands;
import structures.GameState;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import demo.CommandDemo;
import structures.basic.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;


import structures.basic.Tile;
import structures.basic.Unit;


/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

    //board rows & cols
    private static final int B_COLS = 9;
    private static final int B_ROWS = 5;
    private static final int STARTING_HEALTH = 20;
    private static final int STARTING_MANA = 0;

    //starting coordinates
    // all corrdinates here need to be -1 e.g. [2, 3] = [1, 2]      [8, 3] = [7, 2]
    private static final int HUMAN_AVATAR_X = 1; //this equals tile 2 on the board! (loadTile() counts from 1)
    private static final int HUMAN_AVATAR_Y = 2; //same with all tiles below
    private static final int AI_AVATAR_X    = 7;
    private static final int AI_AVATAR_Y    = 2;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);


        /* TODO...
        create board -> e.g. Board board = new Board()
        create decks (human and ai) -> Deck human/aiDeck = CardLoader.getPlayer1/2Cards()
        create avatars -> AvatarUnit human/aiAvatar = new AvatarUnit(??....)
        create playerstates -> PlayerState human/aiState = new PlayerState(PlayerSide.HUMAN_LEFT/AI_RIGHT,human/aiAvatar, human/aiDeck)
        place avatars on correct tiles - DONE
        draw first hand

         */

        //create the board:
        for(int x = 0; x < B_COLS; x++){
            for(int y = 0; y < B_ROWS; y++){
                Tile tile = BasicObjectBuilders.loadTile(x, y);
                BasicCommands.drawTile(out, tile, 0); //should be unhighlighted? confirmed is unhighlighted

            }
        }

        //human initialization stuff
        Tile humanTile = BasicObjectBuilders.loadTile(HUMAN_AVATAR_X, HUMAN_AVATAR_Y);//human goes at tile [2, 3]
        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class); //
        humanAvatar.setPositionByTile(humanTile);


        BasicCommands.drawUnit(out, humanAvatar, humanTile);
        BasicCommands.setUnitAttack(out, humanAvatar, 2);
        BasicCommands.setUnitHealth(out, humanAvatar, STARTING_HEALTH);

        Player humanPlayer = new Player(STARTING_HEALTH, STARTING_MANA);
        BasicCommands.setPlayer1Health(out, humanPlayer);
        BasicCommands.setPlayer1Mana(out, humanPlayer);


        //repeated for ai initialization stuff
        Tile aiTile = BasicObjectBuilders.loadTile(AI_AVATAR_X, AI_AVATAR_Y);
        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);

        aiAvatar.setPositionByTile(aiTile);

        BasicCommands.setUnitAttack(out, aiAvatar, 2);
        BasicCommands.setUnitHealth(out, aiAvatar, STARTING_HEALTH);
        BasicCommands.drawUnit(out, aiAvatar, aiTile);

        Player aiPlayer = new Player(STARTING_HEALTH, STARTING_MANA);
        BasicCommands.setPlayer2Health(out, aiPlayer);
        BasicCommands.setPlayer2Mana(out, aiPlayer);

        //health and attack aren't displaying on the circles at each avatar (on the board) ???








    }

}


