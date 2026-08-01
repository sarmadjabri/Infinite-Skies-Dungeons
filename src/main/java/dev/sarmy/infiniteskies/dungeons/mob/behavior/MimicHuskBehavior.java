package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Mimic Husk. */
public final class MimicHuskBehavior implements MobBehavior { public static final String ID="mimic_husk"; public static final double HEALTH=35; public static final int REVEAL_TICKS=20; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Mimic Husk",EntityType.ZOMBIE,MobRank.NORMAL,HEALTH,6,0.253);} }
