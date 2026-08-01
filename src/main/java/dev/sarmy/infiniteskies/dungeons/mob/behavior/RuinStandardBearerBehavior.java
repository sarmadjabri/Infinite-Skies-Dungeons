package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Ruin Standard-Bearer. */
public final class RuinStandardBearerBehavior implements MobBehavior { public static final String ID="ruin_standard_bearer"; public static final double HEALTH=36,AURA_MULTIPLIER=1.1; public static final int SHOT_COOLDOWN_TICKS=100; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ruin Standard-Bearer",EntityType.PILLAGER,MobRank.NORMAL,HEALTH,2,0.315);} }
