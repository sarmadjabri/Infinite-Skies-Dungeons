package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.*; import org.bukkit.attribute.Attribute; import org.bukkit.entity.*; import java.util.*;

/** Chest-trap controller: warning first, then a normal attacker plus one minion. */
public final class MimicHuskBehavior implements MobBehavior {
 public static final String ID="mimic_husk"; public static final double HEALTH=36; public static final int REVEAL_TICKS=20;
 private final Map<UUID,Long> reveal=new HashMap<>();
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Mimic Husk",EntityType.ZOMBIE,MobRank.NORMAL,HEALTH,7,.253);}
 @Override public void applyEquipment(LivingEntity e){if(!e.getScoreboardTags().contains("isdungeon_mimic_awake"))e.setAI(false);}
 public void tick(LivingEntity e,long tick){if(e.getScoreboardTags().contains("isdungeon_mimic_awake"))return;Player p=BehaviorSupport.target(e,5);if(p==null)return;Long ready=reveal.get(e.getUniqueId());if(ready==null){reveal.put(e.getUniqueId(),tick+REVEAL_TICKS);e.getWorld().playSound(e.getLocation(),Sound.BLOCK_CHEST_LOCKED,1.4f,.65f);return;}if(tick<ready)return;e.addScoreboardTag("isdungeon_mimic_awake");e.setAI(true);CaveSpider minion=(CaveSpider)e.getWorld().spawnEntity(e.getLocation().add(.8,0,0),EntityType.CAVE_SPIDER);minion.getAttribute(Attribute.MAX_HEALTH).setBaseValue(5);minion.setHealth(5);minion.setTarget(p);reveal.remove(e.getUniqueId());}
}
