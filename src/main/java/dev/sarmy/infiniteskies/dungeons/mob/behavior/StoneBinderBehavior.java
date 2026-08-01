package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Stone Binder. */
public final class StoneBinderBehavior implements MobBehavior { public static final String ID="stone_binder"; public static final double HEALTH=30; public static final int BIND_COOLDOWN_TICKS=200,CAST_TICKS=30,ZONE_TICKS=80; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Stone Binder",EntityType.WITCH,MobRank.NORMAL,HEALTH,2,0.25);} }
