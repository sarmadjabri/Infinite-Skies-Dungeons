package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Void Mender. */
public final class VoidMenderBehavior implements MobBehavior {
 public static final String ID="void_mender"; public static final double HEALTH=32, BOLT_DAMAGE=3; public static final int BOLT_COOLDOWN_TICKS=90, EMPOWER_COOLDOWN_TICKS=240;
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Void Mender",EntityType.WITCH,MobRank.NORMAL,HEALTH,2,0.25);}
}
