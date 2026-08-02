package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage; import dev.sarmy.infiniteskies.dungeons.mob.*; import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.util.Vector; import java.util.*;

/** Moving visual fire hazard; its damage is plugin-owned and never lights blocks. */
public final class EmberWispBehavior implements MobBehavior {
 public static final String ID="ember_wisp"; public static final double HEALTH=8,ZONE_DAMAGE=2; public static final int ZONE_COOLDOWN_TICKS=100,ZONE_TICKS=60;
 private final Map<UUID,Long> next=new HashMap<>();private final Map<UUID,Zone> zones=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ember Wisp",EntityType.MAGMA_CUBE,MobRank.NORMAL,HEALTH,0,.36);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){tickZones(tick);Player p=BehaviorSupport.target(e,18);if(p==null)return;Vector v=p.getLocation().toVector().subtract(e.getLocation().toVector());v.setY(0);if(v.lengthSquared()>0)e.setVelocity(v.normalize().multiply(.26).setY(.08));if(next.getOrDefault(e.getUniqueId(),0L)>tick)return;zones.put(UUID.randomUUID(),new Zone(p.getLocation().clone(),tick+ZONE_TICKS));next.put(e.getUniqueId(),tick+ZONE_COOLDOWN_TICKS);}
 private void tickZones(long tick){for(var entry:List.copyOf(zones.entrySet())){Zone z=entry.getValue();if(tick>=z.end()){zones.remove(entry.getKey());continue;}Telegraphs.circle(z.location.getWorld(),z.location,1.4,Particle.FLAME,14);if(tick%20==0)for(Player p:z.location.getWorld().getPlayers())if(!p.isDead()&&p.getLocation().distanceSquared(z.location)<=1.96)ControlledDamage.apply(null,p,ZONE_DAMAGE);}}
 private record Zone(Location location,long end){}
}
