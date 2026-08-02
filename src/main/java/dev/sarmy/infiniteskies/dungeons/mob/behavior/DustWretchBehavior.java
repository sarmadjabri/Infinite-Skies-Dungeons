package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Approved early-room pressure unit: 30 HP, 10 damage, occasional short speed lunge. */
public final class DustWretchBehavior implements MobBehavior {
    public static final String ID = "dust_wretch";
    public static final double HEALTH = 30, DAMAGE = 10, NORMAL_SPEED = .2645D, LUNGE_SPEED = .39675D;
    public static final int LUNGE_TICKS = 30, LUNGE_COOLDOWN_TICKS = 300;
    private final Map<UUID, Long> nextLunge = new HashMap<>();
    private final Map<UUID, Long> lungeEnds = new HashMap<>();

    public String mobId() { return ID; }
    public CustomMobDefinition definition() { return new CustomMobDefinition(ID, "Dust Wretch", EntityType.ZOMBIE, MobRank.NORMAL, HEALTH, DAMAGE, NORMAL_SPEED); }

    @Override public void tick(LivingEntity entity, long tick) {
        if (lungeEnds.getOrDefault(entity.getUniqueId(), 0L) <= tick) setSpeed(entity, NORMAL_SPEED);
        Player target = BehaviorSupport.target(entity, 16);
        if (target == null || nextLunge.getOrDefault(entity.getUniqueId(), 0L) > tick) return;
        double distance = entity.getLocation().distanceSquared(target.getLocation());
        if (distance < 9 || distance > 144) return;
        Vector direction = target.getLocation().toVector().subtract(entity.getLocation().toVector());
        direction.setY(0);
        if (direction.lengthSquared() == 0) return;
        setSpeed(entity, LUNGE_SPEED);
        entity.setVelocity(direction.normalize().multiply(.58).setY(.08));
        lungeEnds.put(entity.getUniqueId(), tick + LUNGE_TICKS);
        nextLunge.put(entity.getUniqueId(), tick + LUNGE_COOLDOWN_TICKS);
    }

    private void setSpeed(LivingEntity entity, double speed) {
        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed);
    }
}
