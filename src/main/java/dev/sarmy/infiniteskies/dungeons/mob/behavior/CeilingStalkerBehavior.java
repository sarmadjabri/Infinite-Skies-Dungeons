package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.*; import org.bukkit.util.Vector; import java.util.*;

/** Still until a player enters range, then makes one damaging drop/leap. */
public final class CeilingStalkerBehavior implements MobBehavior {
 public static final String ID="ceiling_stalker",AMBUSH_TAG="isdungeon_ceiling_ambush"; public static final double HEALTH=20,AMBUSH_DAMAGE=8; public static final int MELEE_COOLDOWN_TICKS=40;
 private final Map<UUID,Long> awakenAt=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ceiling Stalker",EntityType.SPIDER,MobRank.NORMAL,HEALTH,3,.3);}
 @Override public void applyEquipment(LivingEntity e){if(!e.getScoreboardTags().contains("isdungeon_ceiling_awake"))e.setAI(false);}
 public void tick(LivingEntity e,long tick){if(e.getScoreboardTags().contains("isdungeon_ceiling_awake"))return;Player p=BehaviorSupport.target(e,8);if(p==null)return;long release=awakenAt.computeIfAbsent(e.getUniqueId(),ignored->tick+8);if(tick<release)return;Vector leap=p.getLocation().toVector().subtract(e.getLocation().toVector());if(leap.lengthSquared()>0)e.setVelocity(leap.normalize().multiply(.9).setY(.15));e.addScoreboardTag("isdungeon_ceiling_awake");e.addScoreboardTag(AMBUSH_TAG);e.setAI(true);awakenAt.remove(e.getUniqueId());}
}
