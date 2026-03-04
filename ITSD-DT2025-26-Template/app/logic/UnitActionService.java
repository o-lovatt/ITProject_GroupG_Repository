package logic;

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

        if (ActionValidator.isValidMovement(unit.getPosition(), targetTile)) {
            // Update the unit's internal position data
            unit.setPositionByTile(targetTile);

            System.out.println("Unit " + unit.getId() + " moved to Tile(" + targetTile.getTilex() + "," + targetTile.getTiley() + ")");
            // TODO: unit.setHasMoved(true);
            return true;
        }

        System.out.println("Invalid movement attempted!");
        return false;
    }

    /**
     * Attempts to initiate an attack from one unit to another.
     */
    public boolean performAttack(Unit attacker, Unit target) {
        // TODO: Ensure attacker hasn't already attacked this turn

        if (!ActionValidator.isWithinAttackRange(attacker.getPosition(), target.getPosition())) {
            System.out.println("Target is out of range!");
            return false;
        }

        combatResolver.executeAttack(attacker, target);

        // Attacking forfeits movement for the turn
        // TODO: attacker.setHasMoved(true);
        return true;
    }
}