/*package logic;

import structures.basic.Unit;

public class CombatResolver {

    // Note: We use temporary basic logic here until Member 1 and Member 2
    // finish adding 'health' and 'attackPower' attributes to the Unit.java class.

    public CombatResolver() {}

    *//**
     * Executes an attack from an attacker to a target unit.
     *//*
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

    *//**
     * Applies damage to a unit.
     *//*
    private void applyDamage(Unit unit, int damage) {
        // TODO: unit.setHealth(unit.getHealth() - damage);
        System.out.println("Unit " + unit.getId() + " took " + damage + " damage!");

        // TODO: Check if Avatar took damage, and update PlayerState health.
        // TODO: If health <= 0, trigger unit death / game over.
    }
}*/
package logic;

import structures.basic.Unit;

public class CombatResolver {

    public CombatResolver() {}

    public void executeAttack(Unit attacker, Unit target) {
        // 1. Attacker hits target
        applyDamage(target, attacker.getAttack());
        attacker.setHasAttacked(true);
        attacker.setHasMoved(true); // Attacking ends your turn

        // 2. If target survives, it counter-attacks
        if (target.getHealth() > 0) {
            if (ActionValidator.isWithinAttackRange(target.getPosition(), attacker.getPosition())) {
                applyDamage(attacker, target.getAttack());
            }
        }
    }

    private void applyDamage(Unit unit, int damage) {
        int newHealth = unit.getHealth() - damage;
        unit.setHealth(Math.max(0, newHealth)); // Prevent negative health

        System.out.println("Unit " + unit.getId() + " took " + damage + " damage! Health is now: " + unit.getHealth());

        if (unit.getHealth() == 0) {
            System.out.println("Unit " + unit.getId() + " has died!");
            // Later we will tell the frontend to play the death animation here
        }
    }
}