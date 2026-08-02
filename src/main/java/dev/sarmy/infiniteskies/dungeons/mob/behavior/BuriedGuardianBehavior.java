package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.*; import org.bukkit.entity.*; import org.bukkit.util.Vector; import java.util.*;

/** Ground-trigger ambusher; configured spawn position supplies the intended rubble location. */
public final class BuriedGuardianBehavior implements MobBehavior {
 public static final String ID="buried_guardian"; public static final double HEALTH=30; public static final int EMERGE_TICKS=30;
 private final Map<UUID,Long> emerge=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Buried Guardian",EntityType.HUSK,MobRank.NORMAL,HEALTH,6,.23);}
 @Override public void applyEquipment(LivingEntity e){if(!e.getScoreboardTags().contains("isdungeon_buried_awake")){e.setAI(false);e.addScoreboardTag("isdungeon_hide_visual");}}
 public void tick(LivingEntity e,long tick){if(e.getScoreboardTags().contains("isdungeon_buried_awake"))return;Player trigger=BehaviorSupport.target(e,5);if(trigger==null)return;long ready=emerge.computeIfAbsent(e.getUniqueId(),ignored->tick+EMERGE_TICKS);e.getWorld().spawnParticle(Particle.BLOCK,e.getLocation().add(0,.1,0),12,.45,.12,.45,Material.STONE.createBlockData());if(tick<ready)return;e.removeScoreboardTag("isdungeon_hide_visual");e.addScoreboardTag("isdungeon_buried_awake");e.setAI(true);e.getWorld().playSound(e.getLocation(),Sound.BLOCK_STONE_BREAK,1.2f,.7f);for(Player p:e.getWorld().getPlayers())if(!p.isDead()&&p.getLocation().distanceSquared(e.getLocation())<=9){Vector v=p.getLocation().toVector().subtract(e.getLocation().toVector());v.setY(0);if(v.lengthSquared()>0)p.setVelocity(v.normalize().multiply(.55).setY(.2));}emerge.remove(e.getUniqueId());}
}
