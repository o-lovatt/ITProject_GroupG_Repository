package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.PlayerSide;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class CombatResolver {

    public CombatResolver() {}

    public void executeAttack(ActorRef out, GameState gameState, Unit attacker, Unit target) {

        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        applyDamage(out, gameState, target, attacker.getAttackPower());

        // 新增：Human Horn on-hit / avatar damaged / target death
        logic.HumanCardLogic.handleAfterAttackDamage(out, gameState, attacker, target);
        if (target.getId() == 1) {
            logic.HumanCardLogic.handleAvatarDamaged(out, gameState, target);
        }
        //trigger zeal for any ai with zeal when ai avatar takes damage
        if(target.getId() == 2 && !target.isDead()){
            triggerZeal(out, gameState, 2);
        }

        if (target.isDead()) {
            logic.HumanCardLogic.handleUnitDeath(out, gameState, target);
        }

        attacker.setHasAttacked(true);
        attacker.setHasMoved(true);

        if (!target.isDead()) {
            if (ActionValidator.isWithinAttackRange(target.getPosition(), attacker.getPosition())) {

                BasicCommands.playUnitAnimation(out, target, UnitAnimationType.attack);
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

                applyDamage(out, gameState, attacker, target.getAttackPower());

                // 新增：counter damage 后 human avatar robustness / attacker death
                if (attacker.getId() == 1) {
                    logic.HumanCardLogic.handleAvatarDamaged(out, gameState, attacker);
                }
                //added - trigger zeal on counter-attack to AI
                if(attacker.getId() == 2 && !attacker.isDead()){
                    triggerZeal(out, gameState, 2);
                }
                if (attacker.isDead()) {
                    logic.HumanCardLogic.handleUnitDeath(out, gameState, attacker);
                }
            }
        }

        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);
        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.idle);
    }

    //every friendly unit that has zeal gains +2 attack permanently
    private void triggerZeal(ActorRef out, GameState gameState, int avatarOwner){
        for(int x = 1; x <= 9; x++){
            for(int y = 1; y <= 5; y++){
                Unit u = gameState.getUnitAt(x, y);
                if(u !=null && u.getOwner() == avatarOwner && u.hasZeal()){
                    u.setAttack(u.getAttack() +2);
                    u.setAttackPower(u.getAttackPower() +2);
                    BasicCommands.setUnitAttack(out, u, u.getAttack());
                    System.out.println("Unit " + u.getId() + "gained +2 attack " + u.getAttack());
                }
            }
        }
    }

    private void applyDamage(ActorRef out, GameState gameState, Unit unit, int damage) {
        if (unit == null) return;

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
            gameState.removeUnit(unit.getPosition().getTilex(), unit.getPosition().getTiley());

            if (unit.getId() == 1) {
                BasicCommands.addPlayer1Notification(out, "GAME OVER -YOU LOSE!", 100);
                gameState.setGameOver(structures.PlayerSide.AI_RIGHT);
            }else if(unit.getId() == 2){
                BasicCommands.addPlayer1Notification(out, "GAME OVER - YOU WIN!", 100);
                gameState.setGameOver(structures.PlayerSide.HUMAN_LEFT);
                //original gameState.checkWinner() line wasn't doing anything
                //replaced with this now
            }
        }
    }
}