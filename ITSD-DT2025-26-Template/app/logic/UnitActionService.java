package logic;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

public class UnitActionService {
    private CombatResolver combatResolver;

    public UnitActionService(CombatResolver combatResolver) {
        this.combatResolver = combatResolver;
    }
    /**
     * Attempts to move a unit to a specific tile.
     */
    public boolean performMove(Unit unit, Tile targetTile) {
        // TODO: Check if tile is occupied by asking the Board class once it exists
        if(unit.hasMoved()){
            System.out.println("Unit: " + unit.getId() + "has arleady moved this turn!");
            return false;
        }
        if (ActionValidator.isValidMovement(unit.getPosition(), targetTile)) {
            // Update the unit's internal position data
            unit.setPositionByTile(targetTile);
            // TODO: unit.setHasMoved(true);
            //^^ implemented
            unit.setHasMoved(true);

            System.out.println("Unit " + unit.getId() + " moved to Tile(" + targetTile.getTilex() + "," + targetTile.getTiley() + ")");

            return true;
        }

        System.out.println("Invalid movement attempted!");
        return false;
    }

    /**
     * Attempts to initiate an attack from one unit to another.
     */
    public boolean performAttack(ActorRef out, GameState gameState, Unit attacker, Unit target) {
        // TODO: Ensure attacker hasn't already attacked this turn
        //^^ implemented
        if(attacker.hasAttacked()){
            System.out.println("Unit: " + attacker.getId() + "has already attacked this turn!");
            return false;
        }
        if (!ActionValidator.isWithinAttackRange(attacker.getPosition(), target.getPosition())) {
            System.out.println("Target is out of range!");
            return false;
        }

        combatResolver.executeAttack(out, gameState, attacker, target);
        return true;
        // Attacking forfeits movement for the turn
        // TODO: attacker.setHasMoved(true);
    }
}