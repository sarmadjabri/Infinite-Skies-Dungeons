package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Venom Spitter. */
public final class VenomSpitterBehavior implements MobBehavior {
 public static final String ID="venom_spitter"; public static final double HEALTH=22, GLOB_DAMAGE=2; public static final int GLOB_COOLDOWN_TICKS=160, ZONE_TICKS=100, POISON_TICKS=50;
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Venom Spitter",EntityType.CAVE_SPIDER,MobRank.NORMAL,HEALTH,3,0.3);}
}
