package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated live controller definition for Ruin Crawler. */
public final class RuinCrawlerBehavior implements MobBehavior { public static final String ID="ruin_crawler"; public static final double HEALTH=5; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ruin Crawler",EntityType.CAVE_SPIDER,MobRank.NORMAL,HEALTH,4,.3);} }
