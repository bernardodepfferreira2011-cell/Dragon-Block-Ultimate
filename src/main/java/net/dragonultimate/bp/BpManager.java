package net.dragonultimate.bp;

import java.util.HashMap;

import net.minecraft.world.entity.Entity;

public class BpManager {
    public final HashMap<Entity, Integer> ENTITY_BP = new HashMap<>();

    public int getBpMartialArtes(Entity entity, Bp bp) {
        int result = bp.calculateMartialArtesBp();
        ENTITY_BP.put(entity, result);
        return result;
    }

    public int getBpWarrior(Entity entity, Bp bp) {
        int result = bp.calculateWarriorBp();
        ENTITY_BP.put(entity, result);
        return result;
    }

    public int getBpSpiritualist ( Entity entity, Bp bp) {
        int result = bp.calculateSpiritualistBp();
        ENTITY_BP.put(entity, result);
        return result;
    }
}
