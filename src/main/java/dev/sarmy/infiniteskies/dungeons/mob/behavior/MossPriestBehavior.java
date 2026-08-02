package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*;
import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.entity.*; import java.util.*;

/** Avoids combat to channel an interruptible, bounded single-target heal. */
public final class MossPriestBehavior implements MobBehavior {
 public static final String ID="moss_priest"; public static final double HEALTH=35,HEAL_AMOUNT=12; public static final int HEAL_COOLDOWN_TICKS=200,CHANNEL_TICKS=40;
 private final Map<UUID,Cast> casts=new HashMap<>();private final Map<UUID,Long> next=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Moss Priest",EntityType.ZOMBIE_VILLAGER,MobRank.NORMAL,HEALTH,0,.207);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){Cast cast=casts.get(e.getUniqueId());if(cast!=null){Entity target=Bukkit.getEntity(cast.target());if(!(target instanceof LivingEntity ally)||ally.isDead()||e.getHealth()<cast.health()){casts.remove(e.getUniqueId());return;}Telegraphs.line(e.getWorld(),e.getLocation().add(0,1.3,0),ally.getLocation().add(0,1,0),Particle.HAPPY_VILLAGER,8);if(tick>=cast.release()){ally.setHealth(Math.min(ally.getMaxHealth(),ally.getHealth()+HEAL_AMOUNT));e.getWorld().playSound(ally.getLocation(),Sound.BLOCK_MOSS_PLACE,1,.8f);casts.remove(e.getUniqueId());}return;}Player p=BehaviorSupport.target(e,14);if(p!=null)BehaviorSupport.flee(e,p,.28);if(next.getOrDefault(e.getUniqueId(),0L)>tick)return;LivingEntity ally=BehaviorSupport.woundedAlly(e,10);if(ally==null)return;casts.put(e.getUniqueId(),new Cast(ally.getUniqueId(),tick+CHANNEL_TICKS,e.getHealth()));next.put(e.getUniqueId(),tick+HEAL_COOLDOWN_TICKS);}
 private record Cast(UUID target,long release,double health){}
}
