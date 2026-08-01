package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Live combat behavior for the Rift Archer. */
public final class RiftArcherBehavior implements MobBehavior {
 public static final String ID="rift_archer"; public static final double HEALTH=40,WIND_CHARGE_DAMAGE=5; public static final int SHOT_COOLDOWN_TICKS=60,ESCAPE_COOLDOWN_TICKS=100;
 private final ProjectileService projectiles; private final Map<UUID,Long> cooldowns=new HashMap<>(); private final Map<UUID,Cast> casts=new HashMap<>();
 public RiftArcherBehavior(ProjectileService projectiles){this.projectiles=projectiles;}
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Rift Archer",EntityType.STRAY,MobRank.NORMAL,HEALTH,0,.25);}
 public void tick(LivingEntity archer,long tick){
  Player target=CombatTargets.nearestPlayer(archer,20); if(target==null)return; Cast cast=casts.get(archer.getUniqueId());
  if(cast!=null){Telegraphs.line(archer.getWorld(),archer.getLocation().add(0,1.4,0),cast.target,Particle.PORTAL,10); if(tick>=cast.fire){projectiles.launch(archer,cast.target,Material.WIND_CHARGE,1.15,WIND_CHARGE_DAMAGE,tick,50);casts.remove(archer.getUniqueId());}return;}
  if(cooldowns.getOrDefault(archer.getUniqueId(),0L)<=tick){casts.put(archer.getUniqueId(),new Cast(target.getLocation().clone().add(0,1,0),tick+12));cooldowns.put(archer.getUniqueId(),tick+(long)SHOT_COOLDOWN_TICKS);archer.getWorld().playSound(archer.getLocation(),Sound.ENTITY_BREEZE_SHOOT,1,1);}
 }
 private record Cast(Location target,long fire){}
}
