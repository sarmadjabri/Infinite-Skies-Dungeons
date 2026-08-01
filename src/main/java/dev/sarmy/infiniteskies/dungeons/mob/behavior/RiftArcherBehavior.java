package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.entity.EntityType;

/** Dedicated combat holder for the Rift Archer and its future abilities. */
public final class RiftArcherBehavior implements MobBehavior {
    public static final String ID = "rift_archer";
    public static final double HEALTH = 40.0;
    public static final double WIND_CHARGE_DAMAGE = 5.0;
    public static final int SHOT_COOLDOWN_TICKS = 60;
    public static final int ESCAPE_COOLDOWN_TICKS = 100;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Rift Archer", EntityType.STRAY,
                MobRank.NORMAL, HEALTH, 0.0, 0.25);
    }
}

