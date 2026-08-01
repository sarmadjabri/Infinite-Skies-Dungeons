package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Storm Wisp. */
public final class StormWispBehavior implements MobBehavior { public static final String ID="storm_wisp"; public static final double HEALTH=10,LIGHTNING_DAMAGE=5; public static final int MARKER_COOLDOWN_TICKS=120; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Storm Wisp",EntityType.ALLAY,MobRank.NORMAL,HEALTH,1,0.11);} }
