package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.*; import org.bukkit.entity.*; import java.util.*;

/** Motionless statue that audibly cracks, then becomes a real melee threat. */
public final class FalseStatueBehavior implements MobBehavior {
 public static final String ID="false_statue"; public static final double HEALTH=45; public static final int AWAKEN_TICKS=40;
 private final Map<UUID,Long> awaken=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"False Statue",EntityType.ZOMBIE,MobRank.NORMAL,HEALTH,8,.184);}
 @Override public void applyEquipment(LivingEntity e){if(!e.getScoreboardTags().contains("isdungeon_statue_awake"))e.setAI(false);}
 public void tick(LivingEntity e,long tick){if(e.getScoreboardTags().contains("isdungeon_statue_awake"))return;Player p=BehaviorSupport.target(e,7);if(p==null)return;long ready=awaken.computeIfAbsent(e.getUniqueId(),ignored->tick+AWAKEN_TICKS);e.getWorld().spawnParticle(Particle.BLOCK,e.getLocation().add(0,1,0),8,.35,.55,.35,Material.STONE.createBlockData());if(tick<ready)return;e.addScoreboardTag("isdungeon_statue_awake");e.setAI(true);e.getWorld().playSound(e.getLocation(),Sound.BLOCK_STONE_BREAK,1.2f,.75f);awaken.remove(e.getUniqueId());}
}
