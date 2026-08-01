package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Ceiling Stalker. */
public final class CeilingStalkerBehavior implements MobBehavior { public static final String ID="ceiling_stalker"; public static final double HEALTH=18,AMBUSH_DAMAGE=8; public static final int MELEE_COOLDOWN_TICKS=30; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ceiling Stalker",EntityType.SPIDER,MobRank.NORMAL,HEALTH,3,0.3);} }
