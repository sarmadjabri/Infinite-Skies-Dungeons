package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Grave Chanter. */
public final class GraveChanterBehavior implements MobBehavior { public static final String ID="grave_chanter"; public static final double HEALTH=34; public static final int RITUAL_TICKS=80,RITUAL_COOLDOWN_TICKS=400; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Grave Chanter",EntityType.ZOMBIE_VILLAGER,MobRank.NORMAL,HEALTH,3,0.1955);} }
