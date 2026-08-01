package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
/** Dedicated behavior definition for Rift Mine. */
public final class RiftMineBehavior implements MobBehavior {
 public static final String ID="rift_mine"; public static final double HEALTH=12,DETONATION_DAMAGE=10; public static final int TELEGRAPH_TICKS=30;
 private final Map<java.util.UUID,Long> fuses=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Rift Mine",EntityType.SLIME,MobRank.NORMAL,HEALTH,0,0);}
 @Override public void tick(LivingEntity mine,long tick){
  Long fuse=fuses.get(mine.getUniqueId());
  if(fuse==null){ for(Player player:mine.getWorld().getPlayers()) if(!player.isDead()&&player.getLocation().distanceSquared(mine.getLocation())<=20.25){fuses.put(mine.getUniqueId(),tick+TELEGRAPH_TICKS);mine.getWorld().playSound(mine.getLocation(),Sound.BLOCK_NOTE_BLOCK_HAT,1f,.7f);return;} return; }
  mine.getWorld().spawnParticle(Particle.PORTAL,mine.getLocation().add(0,.5,0),12,.8,.2,.8,.05);
  if(tick<fuse)return;
  for(Player player:mine.getWorld().getPlayers()) if(!player.isDead()&&player.getLocation().distanceSquared(mine.getLocation())<=12.25){ControlledDamage.apply(mine,player,DETONATION_DAMAGE);Vector push=player.getLocation().toVector().subtract(mine.getLocation().toVector()).normalize().multiply(.65).setY(.25);player.setVelocity(push);}
  mine.getWorld().spawnParticle(Particle.EXPLOSION,mine.getLocation().add(0,.5,0),1); mine.getWorld().playSound(mine.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1f,1f); fuses.remove(mine.getUniqueId());mine.remove();
 }
}
