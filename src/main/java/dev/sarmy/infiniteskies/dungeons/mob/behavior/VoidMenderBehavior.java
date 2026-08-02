package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService; import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.potion.*; import java.util.*;

/** Fleeting support buffs with a weak self-defense bolt. */
public final class VoidMenderBehavior implements MobBehavior {
 public static final String ID="void_mender"; public static final double HEALTH=32,BOLT_DAMAGE=2; public static final int BOLT_COOLDOWN_TICKS=90,EMPOWER_COOLDOWN_TICKS=240;
 private final ProjectileService projectiles;private final Map<UUID,Long> bolts=new HashMap<>(),buffs=new HashMap<>();
 public VoidMenderBehavior(ProjectileService projectiles){this.projectiles=projectiles;} public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Void Mender",EntityType.WITCH,MobRank.NORMAL,HEALTH,0,.25);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){Player p=BehaviorSupport.target(e,18);if(p!=null)BehaviorSupport.flee(e,p,.25);if(buffs.getOrDefault(e.getUniqueId(),0L)<=tick){LivingEntity ally=BehaviorSupport.nearbyAllies(e,9).stream().findFirst().orElse(null);if(ally!=null){PotionEffectType type=tick%2==0?PotionEffectType.RESISTANCE:PotionEffectType.SPEED;ally.addPotionEffect(new PotionEffect(type,80,0,true,true,true));ally.getWorld().spawnParticle(Particle.PORTAL,ally.getLocation().add(0,1,0),18,.3,.5,.3,.04);buffs.put(e.getUniqueId(),tick+EMPOWER_COOLDOWN_TICKS);return;}}if(p==null||bolts.getOrDefault(e.getUniqueId(),0L)>tick)return;projectiles.launch(e,p.getEyeLocation(),Material.ENDER_PEARL,1.0,BOLT_DAMAGE,tick,55);bolts.put(e.getUniqueId(),tick+BOLT_COOLDOWN_TICKS);}
}
