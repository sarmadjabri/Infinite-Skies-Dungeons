package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.entity.EntityType;

/** Dedicated combat holder for the Sky Reaver and its future abilities. */
public final class SkyReaverBehavior implements MobBehavior {
    public static final String ID = "sky_reaver";
    public static final double HEALTH = 20.0;
    public static final double SWOOP_DAMAGE = 5.0;
    public static final int SWOOP_COOLDOWN_TICKS = 140;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Sky Reaver", EntityType.PHANTOM,
                MobRank.ELITE, HEALTH, SWOOP_DAMAGE, 0.1);
    }
}

