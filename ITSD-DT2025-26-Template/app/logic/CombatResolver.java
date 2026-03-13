package logic;

import structures.basic.Unit;

public class CombatResolver {
    // Note:  use temporary basic logic here until Member 1 and Member 2
    // finish adding 'health' and 'attackPower' attributes to the Unit.java class.

    public CombatResolver() {
    }
    /**
     * Executes an attack from an attacker to a target unit.
     */
    public void executeAttack(Unit attacker, Unit target) {
        // TODO: Get actual attack power when Unit class is updated
        //now implemented ^^
        applyDamage(target, attacker.getAttackPower());
        attacker.setHasAttacked(true);
        attacker.setHasMoved(true);

        //int attackerPower = 2; // Temporary placeholder

        if (!target.isDead()) {
            if (ActionValidator.isWithinAttackRange(target.getPosition(), attacker.getPosition())) {
                applyDamage(attacker, target.getAttackPower());
            }
        }
    }

    /**
     * Applies damage to a unit.
     */
    private void applyDamage(Unit unit, int damage) {
        unit.takeDamage(damage);
        System.out.println("Unit: " + unit.getId() + "Damage: " + damage + "Current Health: " + unit.getHealth());

        if (unit.isDead()) {
            System.out.println("Unit: " + unit.getId() + "is dead!");
            //TODO NEED TO ACTIVATE DEATHWATCH, ROMVE FROM BOARD AND CHECK WINNING CONDITIONS
        }


        // TODO: attacker.setHasAttacked(true);

        // TODO: Check if target health is > 0 before allowing counter-attack

    }
}