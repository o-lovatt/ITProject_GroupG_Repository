package events;

import structures.GameState;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import demo.CommandDemo;

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

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;
		
		// User 1 makes a change
		CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);


        /* TODO...
        create board -> e.g. Board board = new Board()
        create decks (human and ai) -> Deck human/aiDeck = CardLoader.getPlayer1/2Cards()
        create avatars -> AvatarUnit human/aiAvatar = new AvatarUnit(??....)
        create playerstates -> PlayerState human/aiState = new PlayerState(PlayerSide.HUMAN_LEFT/AI_RIGHT,human/aiAvatar, human/aiDeck)
        place avatars on correct tiles
        draw first hand

         */
	}

}


