package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Ember Wisp. */
public final class EmberWispBehavior implements MobBehavior { public static final String ID="ember_wisp"; public static final double HEALTH=8; public static final int ZONE_COOLDOWN_TICKS=100,ZONE_TICKS=60; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ember Wisp",EntityType.MAGMA_CUBE,MobRank.NORMAL,HEALTH,2,0.36);} }
