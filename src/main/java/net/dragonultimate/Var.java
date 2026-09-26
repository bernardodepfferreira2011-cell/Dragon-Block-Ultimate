package net.dragonultimate;

import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class Var {
    public String Transformation;
    public final String Warrior = "Warrior";
    public final String Spiritualist = "Spiritualist";
    public final String MartialArtes = "MartialArtes";
    public int level;
    public Map<Entity, Transformation> FORM = new HashMap<>();
    public Map<Entity, DragonBlockUltimate> ENTITY = new HashMap<>();

    public record Transformation( String name ) {
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
