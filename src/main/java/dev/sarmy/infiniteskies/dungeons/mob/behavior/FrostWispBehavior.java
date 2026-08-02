package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService; import dev.sarmy.infiniteskies.dungeons.mob.*; import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.potion.*; import java.util.*;

/** Low-damage orb that creates a short Slow-I patch near stronger enemies. */
public final class FrostWispBehavior implements MobBehavior {
 public static final String ID="frost_wisp"; public static final double HEALTH=10,ORB_DAMAGE=1; public static final int ORB_COOLDOWN_TICKS=120,ZONE_TICKS=80;
 private final ProjectileService projectiles;private final Map<UUID,Long> next=new HashMap<>();private final Map<UUID,Zone> zones=new HashMap<>();
 public FrostWispBehavior(ProjectileService projectiles){this.projectiles=projectiles;} public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Frost Wisp",EntityType.ALLAY,MobRank.NORMAL,HEALTH,0,.1);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){tickZones(tick);Player p=BehaviorSupport.target(e,18);if(p==null)return;if(next.getOrDefault(e.getUniqueId(),0L)>tick)return;projectiles.launch(e,p.getLocation().add(0,.7,0),Material.SNOWBALL, .85,ORB_DAMAGE,tick,65,(hit,player)->zones.put(UUID.randomUUID(),new Zone(player.getLocation().clone(),tick+ZONE_TICKS)));next.put(e.getUniqueId(),tick+ORB_COOLDOWN_TICKS);}
 private void tickZones(long tick){for(var entry:List.copyOf(zones.entrySet())){Zone z=entry.getValue();if(tick>=z.end()){zones.remove(entry.getKey());continue;}Telegraphs.circle(z.location.getWorld(),z.location,1.6,Particle.SNOWFLAKE,14);if(tick%10==0)for(Player p:z.location.getWorld().getPlayers())if(!p.isDead()&&p.getLocation().distanceSquared(z.location)<=2.56)p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,40,0,true,true,true));}}
 private record Zone(Location location,long end){}
}
