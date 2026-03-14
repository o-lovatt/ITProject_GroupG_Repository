package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class CombatResolver {

    public CombatResolver() {}

    public void executeAttack(ActorRef out, GameState gameState, Unit attacker, Unit target) {

        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        applyDamage(out, gameState, target, attacker.getAttackPower());
        attacker.setHasAttacked(true);
        attacker.setHasMoved(true);

        if (!target.isDead()) {
            if (ActionValidator.isWithinAttackRange(target.getPosition(), attacker.getPosition())) {

                BasicCommands.playUnitAnimation(out, target, UnitAnimationType.attack);
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

                applyDamage(out, gameState, attacker, target.getAttackPower());
            }
        }

        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);
        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.idle);
    }

    private void applyDamage(ActorRef out, GameState gameState, Unit unit, int damage) {
        unit.takeDamage(damage);
        System.out.println("Unit: " + unit.getId() + " get " + damage + " damage, health now: " + unit.getHealth());

        BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);

        BasicCommands.setUnitHealth(out, unit, unit.getHealth());

        if (unit.getId() == 1) {
            gameState.player1.setHealth(unit.getHealth());
            BasicCommands.setPlayer1Health(out, gameState.player1);
        }
        else if (unit.getId() == 2) {
            gameState.player2.setHealth(unit.getHealth());
            BasicCommands.setPlayer2Health(out, gameState.player2);
        }

        if (unit.isDead()) {
            System.out.println("Unit: " + unit.getId() + " out!");
            BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

            BasicCommands.deleteUnit(out, unit);
            // TODO: 这里将来需要调用 GameState 来移除 boardUnits 中的记录
        }
    }
}