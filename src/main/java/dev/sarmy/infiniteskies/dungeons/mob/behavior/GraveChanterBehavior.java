package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.*; import org.bukkit.attribute.Attribute; import org.bukkit.entity.*; import java.util.*;

/** Four-second ritual that produces a capped, tracked pair of short-lived Stone Scarabs. */
public final class GraveChanterBehavior implements MobBehavior {
 public static final String ID="grave_chanter"; public static final double HEALTH=40; public static final int RITUAL_TICKS=80,RITUAL_COOLDOWN_TICKS=400,SCARAB_LIFETIME_TICKS=600;
 private final Map<UUID,Cast> casts=new HashMap<>();private final Map<UUID,Long> next=new HashMap<>();private final Map<UUID,List<Minion>> minions=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Grave Chanter",EntityType.ZOMBIE_VILLAGER,MobRank.NORMAL,HEALTH,0,.1955);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){cleanup(e.getUniqueId(),tick);Cast cast=casts.get(e.getUniqueId());if(cast!=null){Telegraphs.circle(e.getWorld(),e.getLocation(),2.2,Particle.SOUL,18);if(tick>=cast.release()){spawnScarabs(e,cast.target(),tick);casts.remove(e.getUniqueId());}return;}Player p=BehaviorSupport.target(e,20);if(p==null||next.getOrDefault(e.getUniqueId(),0L)>tick)return;casts.put(e.getUniqueId(),new Cast(p.getUniqueId(),tick+RITUAL_TICKS));next.put(e.getUniqueId(),tick+RITUAL_COOLDOWN_TICKS);e.getWorld().playSound(e.getLocation(),Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, .5f,1.6f);}
 private void spawnScarabs(LivingEntity source,UUID target,long tick){List<Minion> list=minions.computeIfAbsent(source.getUniqueId(),ignored->new ArrayList<>());for(int i=0;i<2;i++){Silverfish scarab=(Silverfish)source.getWorld().spawnEntity(source.getLocation().add((i==0?1:-1),0,.5),EntityType.SILVERFISH);scarab.getAttribute(Attribute.MAX_HEALTH).setBaseValue(6);scarab.setHealth(6);scarab.customName(null);scarab.setRemoveWhenFarAway(false);Player p=Bukkit.getPlayer(target);if(p!=null)scarab.setTarget(p);list.add(new Minion(scarab.getUniqueId(),tick+SCARAB_LIFETIME_TICKS));}}
 private void cleanup(UUID id,long tick){List<Minion> list=minions.get(id);if(list==null)return;list.removeIf(minion->{Entity entity=Bukkit.getEntity(minion.entity());if(entity==null||!entity.isValid()||tick>=minion.end()){if(entity!=null)entity.remove();return true;}return false;});if(list.isEmpty())minions.remove(id);}
 public void onControllerRemoved(UUID id){for(Minion m:minions.getOrDefault(id,List.of())){Entity e=Bukkit.getEntity(m.entity());if(e!=null)e.remove();}minions.remove(id);}
 private record Cast(UUID target,long release){} private record Minion(UUID entity,long end){}
}
