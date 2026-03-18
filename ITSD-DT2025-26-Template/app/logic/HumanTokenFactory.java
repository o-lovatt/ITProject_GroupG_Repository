package logic;

import structures.basic.Unit;
import utils.BasicObjectBuilders;

public class HumanTokenFactory {

    private static int nextTokenId = 5000;
    private static final String WRAITHLING_CONF = "conf/gameconfs/units/wraithling.json";

    private HumanTokenFactory() {}

    public static Unit createWraithling(int owner) {
        Unit unit = BasicObjectBuilders.loadUnit(WRAITHLING_CONF, nextTokenId++, Unit.class);
        unit.setOwner(owner);

        unit.setHealth(1);
        unit.setMaxHealth(1);
        unit.setAttack(1);
        unit.setAttackPower(1);

        // summoning sickness
        unit.setHasMoved(true);
        unit.setHasAttacked(true);

        return unit;
    }
}