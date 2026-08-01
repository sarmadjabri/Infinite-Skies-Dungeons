package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.entity.EntityType;

/** Dedicated combat holder for the Storm Caller and its future abilities. */
public final class StormCallerBehavior implements MobBehavior {
    public static final String ID = "storm_caller";
    public static final double HEALTH = 50.0;
    public static final double TRIDENT_DAMAGE = 10.0;
    public static final double LIGHTNING_DAMAGE = 7.0;
    public static final int TRIDENT_COOLDOWN_TICKS = 100;
    public static final int SPIN_DURATION_TICKS = 60;
    public static final int SPIN_COOLDOWN_TICKS = 500;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Storm Caller", EntityType.DROWNED,
                MobRank.NORMAL, HEALTH, 0.0, 0.253);
    }
}

