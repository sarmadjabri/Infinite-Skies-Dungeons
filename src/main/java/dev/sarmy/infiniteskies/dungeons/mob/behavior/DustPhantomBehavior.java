package dev.sarmy.infiniteskies.dungeons.mob.behavior;
import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.EntityType;
/** Dedicated behavior definition for Dust Phantom. */
public final class DustPhantomBehavior implements MobBehavior { public static final String ID="dust_phantom"; public static final double HEALTH=6; public static final int LIFETIME_TICKS=240,SWOOP_COOLDOWN_TICKS=50; public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Dust Phantom",EntityType.VEX,MobRank.NORMAL,HEALTH,4,0.595);} }
