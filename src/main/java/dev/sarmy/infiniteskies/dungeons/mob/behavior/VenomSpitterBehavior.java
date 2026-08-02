package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.potion.*;
import java.util.*;

/** Visible venom glob followed by a short, mild poison puddle. */
public final class VenomSpitterBehavior implements MobBehavior {
 public static final String ID="venom_spitter"; public static final double HEALTH=24,GLOB_DAMAGE=2; public static final int GLOB_COOLDOWN_TICKS=140,ZONE_TICKS=100,POISON_TICKS=50;
 private final ProjectileService projectiles; private final Map<UUID,Long> next=new HashMap<>(); private final Map<UUID,Zone> zones=new HashMap<>();
 public VenomSpitterBehavior(ProjectileService projectiles){this.projectiles=projectiles;} public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Venom Spitter",EntityType.CAVE_SPIDER,MobRank.NORMAL,HEALTH,0,.3);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){tickZones(tick);Player p=BehaviorSupport.target(e,18);if(p==null)return;double d=e.getLocation().distanceSquared(p.getLocation());if(d<36)BehaviorSupport.flee(e,p,.35);if(next.getOrDefault(e.getUniqueId(),0L)>tick)return;projectiles.launch(e,p.getLocation().add(0,.8,0),Material.SLIME_BALL,1.0,GLOB_DAMAGE,tick,60,(hit,player)->addZone(player.getLocation(),tick));next.put(e.getUniqueId(),tick+GLOB_COOLDOWN_TICKS);}
 private void addZone(Location l,long tick){UUID id=UUID.randomUUID();zones.put(id,new Zone(l.clone(),tick+ZONE_TICKS));}
 private void tickZones(long tick){for(var entry:List.copyOf(zones.entrySet())){Zone z=entry.getValue();if(tick>=z.end()){zones.remove(entry.getKey());continue;}Telegraphs.circle(z.location.getWorld(),z.location,1.5,Particle.SNEEZE,12);if(tick%20!=0)continue;for(Player p:z.location.getWorld().getPlayers())if(!p.isDead()&&p.getLocation().distanceSquared(z.location)<=2.25)p.addPotionEffect(new PotionEffect(PotionEffectType.POISON,POISON_TICKS,0,true,true,true));}}
 private record Zone(Location location,long end){}
}
