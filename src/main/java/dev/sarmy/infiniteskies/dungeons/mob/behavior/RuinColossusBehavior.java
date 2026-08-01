package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.combat.*; import dev.sarmy.infiniteskies.dungeons.mob.*; import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import java.util.*;
/** Live Warden controller with sonic boom disabled and a controlled stone throw. */
public final class RuinColossusBehavior implements MobBehavior {
 public static final String ID="ruin_colossus"; public static final double HEALTH=100,MELEE_DAMAGE=15,BLOCK_THROW_DAMAGE=15; public static final int BLOCK_THROW_COOLDOWN_TICKS=100;
 private final ProjectileService projectiles; private final Map<UUID,Long> next=new HashMap<>(); private final Map<UUID,Cast> casts=new HashMap<>();
 public RuinColossusBehavior(ProjectileService projectiles){this.projectiles=projectiles;} public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ruin Colossus",EntityType.WARDEN,MobRank.ELITE,HEALTH,MELEE_DAMAGE,.3);}
 public void tick(LivingEntity warden,long tick){Player target=CombatTargets.nearestPlayer(warden,20);if(target==null)return;Cast cast=casts.get(warden.getUniqueId());if(cast!=null){Telegraphs.line(warden.getWorld(),warden.getLocation().add(0,2,0),cast.point,Particle.BLOCK,10);if(tick>=cast.release){projectiles.launch(warden,cast.point,Material.COBBLESTONE,1.0,BLOCK_THROW_DAMAGE,tick,60);casts.remove(warden.getUniqueId());}return;}if(next.getOrDefault(warden.getUniqueId(),0L)<=tick&&warden.getLocation().distanceSquared(target.getLocation())>25){casts.put(warden.getUniqueId(),new Cast(target.getLocation().clone().add(0,1,0),tick+20));next.put(warden.getUniqueId(),tick+BLOCK_THROW_COOLDOWN_TICKS);warden.getWorld().playSound(warden.getLocation(),Sound.BLOCK_STONE_PLACE,1,.55f);}}
 private record Cast(Location point,long release){}
}
