package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.potion.*; import java.util.*;

/** Player gets a visible escape window before a mild slowing circle activates. */
public final class StoneBinderBehavior implements MobBehavior {
 public static final String ID="stone_binder"; public static final double HEALTH=34; public static final int BIND_COOLDOWN_TICKS=200,CAST_TICKS=30,ZONE_TICKS=80;
 private final Map<UUID,Cast> casts=new HashMap<>();private final Map<UUID,Long> next=new HashMap<>();private final Map<UUID,Zone> zones=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Stone Binder",EntityType.WITCH,MobRank.NORMAL,HEALTH,0,.25);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){tickZones(tick);Cast cast=casts.get(e.getUniqueId());if(cast!=null){Telegraphs.circle(e.getWorld(),cast.point(),2.0,Particle.BLOCK_MARKER,18);if(tick>=cast.release()){zones.put(UUID.randomUUID(),new Zone(cast.point(),tick+ZONE_TICKS));casts.remove(e.getUniqueId());}return;}Player p=BehaviorSupport.target(e,20);if(p==null||next.getOrDefault(e.getUniqueId(),0L)>tick)return;casts.put(e.getUniqueId(),new Cast(p.getLocation().clone(),tick+CAST_TICKS));next.put(e.getUniqueId(),tick+BIND_COOLDOWN_TICKS);}
 private void tickZones(long tick){for(var entry:List.copyOf(zones.entrySet())){Zone z=entry.getValue();if(tick>=z.end()){zones.remove(entry.getKey());continue;}Telegraphs.circle(z.location.getWorld(),z.location,2,Particle.DUST_PLUME,18);if(tick%10!=0)continue;for(Player p:z.location.getWorld().getPlayers())if(!p.isDead()&&p.getLocation().distanceSquared(z.location)<=4)p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,25,0,true,true,true));}}
 private record Cast(Location point,long release){} private record Zone(Location location,long end){}
}
