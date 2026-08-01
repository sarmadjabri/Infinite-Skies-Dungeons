package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Frost Wisp. */
public final class FrostWispBehavior implements MobBehavior { public static final String ID="frost_wisp"; public static final double HEALTH=10,ORB_DAMAGE=1; public static final int ORB_COOLDOWN_TICKS=120,ZONE_TICKS=80; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Frost Wisp",EntityType.ALLAY,MobRank.NORMAL,HEALTH,1,0.1);} }
