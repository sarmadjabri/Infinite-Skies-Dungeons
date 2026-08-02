package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.*;

/** Mobile ranged knockback threat; it never deals uncontrolled Breeze damage. */
public final class GaleAcolyteBehavior implements MobBehavior {
 public static final String ID="gale_acolyte"; public static final double HEALTH=26, WIND_BOLT_DAMAGE=2; public static final int BOLT_COOLDOWN_TICKS=80, REPOSITION_COOLDOWN_TICKS=100;
 private final ProjectileService projectiles; private final Map<UUID,Long> bolts=new HashMap<>(), reposition=new HashMap<>();
 public GaleAcolyteBehavior(ProjectileService projectiles){this.projectiles=projectiles;} public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Gale Acolyte",EntityType.BREEZE,MobRank.NORMAL,HEALTH,0,.63);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){Player p=BehaviorSupport.target(e,24);if(p==null)return; if(reposition.getOrDefault(e.getUniqueId(),0L)<=tick){BehaviorSupport.sidestep(e,p,.55);reposition.put(e.getUniqueId(),tick+REPOSITION_COOLDOWN_TICKS);} if(bolts.getOrDefault(e.getUniqueId(),0L)>tick)return; BehaviorSupport.face(e,p.getEyeLocation());projectiles.launch(e,p.getEyeLocation(),Material.WIND_CHARGE,1.25,WIND_BOLT_DAMAGE,tick,55,(hit,player)->{Vector v=player.getLocation().toVector().subtract(hit.getLocation().toVector());v.setY(0);if(v.lengthSquared()>0)player.setVelocity(v.normalize().multiply(.72).setY(.18));player.getWorld().spawnParticle(Particle.CLOUD,player.getLocation().add(0,1,0),16,.3,.4,.3,.03);});bolts.put(e.getUniqueId(),tick+BOLT_COOLDOWN_TICKS);}
}
