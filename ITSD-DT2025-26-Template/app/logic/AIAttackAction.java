package logic;

import structures.basic.Unit;

public class AIAttackAction {
    public Unit attacker;
    public Unit target;

    public AIAttackAction(Unit attacker, Unit target) {
        this.attacker = attacker;
        this.target = target;
    }
}