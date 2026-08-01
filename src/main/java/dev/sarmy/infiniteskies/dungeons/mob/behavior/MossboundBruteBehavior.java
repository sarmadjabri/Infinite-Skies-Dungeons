package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.entity.EntityType;

/** Dedicated combat holder for the Mossbound Brute and its future abilities. */
public final class MossboundBruteBehavior implements MobBehavior {
    public static final String ID = "mossbound_brute";
    public static final double HEALTH = 40.0;
    public static final double MELEE_DAMAGE = 17.0;
    public static final double BLOCK_THROW_DAMAGE = 10.0;
    public static final int BLOCK_THROW_COOLDOWN_TICKS = 100;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Mossbound Brute", EntityType.IRON_GOLEM,
                MobRank.NORMAL, HEALTH, MELEE_DAMAGE, 0.25);
    }
}

