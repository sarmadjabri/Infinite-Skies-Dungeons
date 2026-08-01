package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.entity.EntityType;

/** Dedicated combat holder for the Ruin Colossus and its future abilities. */
public final class RuinColossusBehavior implements MobBehavior {
    public static final String ID = "ruin_colossus";
    public static final double HEALTH = 100.0;
    public static final double MELEE_DAMAGE = 15.0;
    public static final double BLOCK_THROW_DAMAGE = 15.0;
    public static final int BLOCK_THROW_COOLDOWN_TICKS = 100;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Ruin Colossus", EntityType.WARDEN,
                MobRank.ELITE, HEALTH, MELEE_DAMAGE, 0.3);
    }
}
