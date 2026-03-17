package logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class SpellService {

    public static void castTrueStrike(ActorRef out, GameState gameState, Unit target) {
        System.out.println("Casting True Strike on Unit " + target.getId());

        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.hit);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        target.takeDamage(2);
        BasicCommands.setUnitHealth(out, target, target.getHealth());

        if (target.getId() == 1) {
            gameState.player1.setHealth(target.getHealth());
            BasicCommands.setPlayer1Health(out, gameState.player1);
        } else if (target.getId() == 2) {
            gameState.player2.setHealth(target.getHealth());
            BasicCommands.setPlayer2Health(out, gameState.player2);
        }

        if (target.isDead()) {
            BasicCommands.playUnitAnimation(out, target, UnitAnimationType.death);
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            BasicCommands.deleteUnit(out, target);
            if (target.getId() == 1 || target.getId() == 2) {
                gameState.checkWinner();
            }
        }

        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.idle);
    }

    public static void castSundropElixir(ActorRef out, GameState gameState, Unit target) {
        System.out.println("Casting Sundrop Elixir on Unit " + target.getId());

        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.channel);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        target.heal(5);
        BasicCommands.setUnitHealth(out, target, target.getHealth());

        if (target.getId() == 1) {
            gameState.player1.setHealth(target.getHealth());
            BasicCommands.setPlayer1Health(out, gameState.player1);
        } else if (target.getId() == 2) {
            gameState.player2.setHealth(target.getHealth());
            BasicCommands.setPlayer2Health(out, gameState.player2);
        }

        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.idle);
    }

    public static void castBeamShock(ActorRef out, GameState gameState, Unit target) {
        System.out.println("Casting Beam Shock on Unit " + target.getId());

        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.hit);
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        target.setStunned(true);

        BasicCommands.addPlayer1Notification(out, "Unit Stunned!", 2);
        BasicCommands.playUnitAnimation(out, target, UnitAnimationType.idle);
    }
}