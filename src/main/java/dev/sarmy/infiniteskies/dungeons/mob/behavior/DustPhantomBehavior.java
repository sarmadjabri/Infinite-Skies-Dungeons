package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage; import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.util.Vector; import java.util.*;

/** Short-lived, max-two-hit wall ambusher with a hard twelve-second lifetime. */
public final class DustPhantomBehavior implements MobBehavior {
 public static final String ID="dust_phantom"; public static final double HEALTH=8; public static final int LIFETIME_TICKS=240,STRIKE_COOLDOWN_TICKS=60;
 private final Map<UUID,State> states=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Dust Phantom",EntityType.VEX,MobRank.NORMAL,HEALTH,0,.595);}
 @Override public void applyEquipment(LivingEntity e){e.setAI(false);}
 public void tick(LivingEntity e,long tick){State state=states.computeIfAbsent(e.getUniqueId(),ignored->new State(tick+LIFETIME_TICKS,0,0));if(tick>=state.end()){e.remove();states.remove(e.getUniqueId());return;}Player p=BehaviorSupport.target(e,18);if(p==null||state.hits()>=2)return;Vector v=p.getLocation().add(0,.8,0).toVector().subtract(e.getLocation().toVector());if(v.lengthSquared()>0)e.setVelocity(v.normalize().multiply(.48));if(state.next()>tick||e.getLocation().distanceSquared(p.getLocation())>2.25)return;ControlledDamage.apply(e,p,4);p.getWorld().spawnParticle(Particle.ASH,p.getLocation().add(0,1,0),12,.25,.4,.25,.02);states.put(e.getUniqueId(),new State(state.end(),state.hits()+1,tick+STRIKE_COOLDOWN_TICKS));}
 private record State(long end,int hits,long next){}
}
