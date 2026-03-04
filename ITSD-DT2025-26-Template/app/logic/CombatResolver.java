package logic;

import structures.basic.Unit;

public class CombatResolver {

    // Note: We use temporary basic logic here until Member 1 and Member 2
    // finish adding 'health' and 'attackPower' attributes to the Unit.java class.

    public CombatResolver() {}

    /**
     * Executes an attack from an attacker to a target unit.
     */
    public void executeAttack(Unit attacker, Unit target) {
        // TODO: Get actual attack power when Unit class is updated
        int attackerPower = 2; // Temporary placeholder

        applyDamage(target, attackerPower);
        // TODO: attacker.setHasAttacked(true);

        // TODO: Check if target health is > 0 before allowing counter-attack
        boolean targetSurvived = true;

        if (targetSurvived) {
            // Target counter-attacks if in range
            if (ActionValidator.isWithinAttackRange(target.getPosition(), attacker.getPosition())) {
                int targetPower = 2; // Temporary placeholder
                applyDamage(attacker, targetPower);
            }
        }
    }

    /**
     * Applies damage to a unit.
     */
    private void applyDamage(Unit unit, int damage) {
        // TODO: unit.setHealth(unit.getHealth() - damage);
        System.out.println("Unit " + unit.getId() + " took " + damage + " damage!");

        // TODO: Check if Avatar took damage, and update PlayerState health.
        // TODO: If health <= 0, trigger unit death / game over.
    }
}