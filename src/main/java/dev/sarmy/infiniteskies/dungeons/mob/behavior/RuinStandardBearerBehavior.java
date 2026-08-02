package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.*; import org.bukkit.entity.*; import org.bukkit.potion.*;

/** Passive 10-block formation aura; it vanishes automatically as soon as this mob dies. */
public final class RuinStandardBearerBehavior implements MobBehavior {
 public static final String ID="ruin_standard_bearer"; public static final double HEALTH=40; public static final int AURA_RADIUS=10;
 public String mobId(){return ID;} public CustomMobDefinition definition(){return new CustomMobDefinition(ID,"Ruin Standard-Bearer",EntityType.PILLAGER,MobRank.NORMAL,HEALTH,2,.315);}
 public void tick(LivingEntity e,long tick){if(tick%10!=0)return;for(LivingEntity ally:BehaviorSupport.nearbyAllies(e,AURA_RADIUS))ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,30,0,true,false,true));}
}
