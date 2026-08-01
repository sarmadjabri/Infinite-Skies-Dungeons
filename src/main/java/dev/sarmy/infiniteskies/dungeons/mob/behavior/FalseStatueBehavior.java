package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for False Statue. */
public final class FalseStatueBehavior implements MobBehavior { public static final String ID="false_statue"; public static final double HEALTH=45; public static final int AWAKEN_TICKS=40,MELEE_COOLDOWN_TICKS=50; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"False Statue",EntityType.ZOMBIE,MobRank.NORMAL,HEALTH,7,0.184);} }
