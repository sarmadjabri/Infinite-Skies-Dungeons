package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Observatory Watcher. */
public final class ObservatoryWatcherBehavior implements MobBehavior {
 public static final String ID="observatory_watcher"; public static final double HEALTH=35, CHARGED_SHOT_DAMAGE=12; public static final int SHOT_COOLDOWN_TICKS=160, TELEGRAPH_TICKS=30, RELOAD_TICKS=60;
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Observatory Watcher",EntityType.SKELETON,MobRank.NORMAL,HEALTH,2,0.025);}
}
