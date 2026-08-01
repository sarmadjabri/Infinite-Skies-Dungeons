package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Buried Guardian. */
public final class BuriedGuardianBehavior implements MobBehavior { public static final String ID="buried_guardian"; public static final double HEALTH=28; public static final int EMERGE_TICKS=30; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Buried Guardian",EntityType.HUSK,MobRank.NORMAL,HEALTH,6,0.23);} }
