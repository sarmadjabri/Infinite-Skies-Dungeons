package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.combat.*; import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.*; import org.bukkit.entity.*; import java.util.*;
/** Live controlled trident-and-lightning combat behavior. */
public final class StormCallerBehavior implements MobBehavior {
 public static final String ID="storm_caller"; public static final double HEALTH=50,TRIDENT_DAMAGE=10,LIGHTNING_DAMAGE=7; public static final int TRIDENT_COOLDOWN_TICKS=100,SPIN_DURATION_TICKS=60,SPIN_COOLDOWN_TICKS=500;
 private final ProjectileService projectiles; private final Map<UUID,Long> cooldowns=new HashMap<>();
 public StormCallerBehavior(ProjectileService projectiles){this.projectiles=projectiles;} public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Storm Caller",EntityType.DROWNED,MobRank.NORMAL,HEALTH,0,.253);}
 public void tick(LivingEntity drowned,long tick){Player target=CombatTargets.nearestPlayer(drowned,18);if(target==null||cooldowns.getOrDefault(drowned.getUniqueId(),0L)>tick)return; Location point=target.getLocation().clone().add(0,1,0);drowned.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,point,12,.3,.4,.3,.03);projectiles.launch(drowned,point,Material.TRIDENT,1.05,TRIDENT_DAMAGE,tick,60,(hit,player)->{player.getWorld().strikeLightningEffect(player.getLocation());ControlledDamage.apply(hit,player,LIGHTNING_DAMAGE);});cooldowns.put(drowned.getUniqueId(),tick+TRIDENT_COOLDOWN_TICKS);drowned.getWorld().playSound(drowned.getLocation(),Sound.ITEM_TRIDENT_THROW,1,1);}
}
