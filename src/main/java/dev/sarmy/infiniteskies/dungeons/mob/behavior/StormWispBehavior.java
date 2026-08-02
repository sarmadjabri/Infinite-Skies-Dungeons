package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage; import dev.sarmy.infiniteskies.dungeons.mob.*; import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import java.util.*;

/** A mobile lightning warning with a one-second escape window. */
public final class StormWispBehavior implements MobBehavior {
 public static final String ID="storm_wisp"; public static final double HEALTH=10,LIGHTNING_DAMAGE=5; public static final int MARKER_COOLDOWN_TICKS=120,CAST_TICKS=20;
 private final Map<UUID,Long> next=new HashMap<>();private final Map<UUID,Cast> casts=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Storm Wisp",EntityType.ALLAY,MobRank.NORMAL,HEALTH,0,.11);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){Player p=BehaviorSupport.target(e,18);Cast cast=casts.get(e.getUniqueId());if(cast!=null){Telegraphs.circle(e.getWorld(),cast.point(),1.25,Particle.ELECTRIC_SPARK,14);if(tick>=cast.release()){e.getWorld().strikeLightningEffect(cast.point());for(Player hit:e.getWorld().getPlayers())if(!hit.isDead()&&hit.getLocation().distanceSquared(cast.point())<=1.56)ControlledDamage.apply(e,hit,LIGHTNING_DAMAGE);casts.remove(e.getUniqueId());}return;}if(p==null)return;BehaviorSupport.sidestep(e,p,.26);if(next.getOrDefault(e.getUniqueId(),0L)>tick)return;casts.put(e.getUniqueId(),new Cast(p.getLocation().clone(),tick+CAST_TICKS));next.put(e.getUniqueId(),tick+MARKER_COOLDOWN_TICKS);}
 private record Cast(Location point,long release){}
}
