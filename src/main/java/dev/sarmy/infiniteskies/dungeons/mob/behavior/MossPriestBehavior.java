package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*;
import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Moss Priest. */
public final class MossPriestBehavior implements MobBehavior {
 public static final String ID="moss_priest"; public static final double HEALTH=30, HEAL_AMOUNT=12; public static final int HEAL_COOLDOWN_TICKS=200, CHANNEL_TICKS=50;
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Moss Priest",EntityType.ZOMBIE_VILLAGER,MobRank.NORMAL,HEALTH,3,0.207);}
}
