package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Gale Acolyte. */
public final class GaleAcolyteBehavior implements MobBehavior {
 public static final String ID="gale_acolyte"; public static final double HEALTH=28, WIND_BOLT_DAMAGE=3; public static final int BOLT_COOLDOWN_TICKS=80, REPOSITION_COOLDOWN_TICKS=100;
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Gale Acolyte",EntityType.BREEZE,MobRank.NORMAL,HEALTH,2,0.63);}
}
