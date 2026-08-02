package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.List;

/** Small bounded helpers shared by live custom-mob behaviors. */
final class BehaviorSupport {
    private BehaviorSupport() { }

    static Player target(LivingEntity source, double range) {
        return CombatTargets.nearestPlayer(source, range);
    }

    static void face(LivingEntity source, Location target) {
        Location facing = source.getLocation();
        facing.setDirection(target.toVector().subtract(facing.toVector()));
        source.setRotation(facing.getYaw(), facing.getPitch());
    }

    static void flee(LivingEntity entity, Player player, double speed) {
        Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector());
        direction.setY(0.0);
        if (direction.lengthSquared() > .001D) entity.setVelocity(direction.normalize().multiply(speed));
    }

    static void sidestep(LivingEntity entity, Player player, double speed) {
        Vector direction = player.getLocation().toVector().subtract(entity.getLocation().toVector());
        direction.setY(0.0);
        if (direction.lengthSquared() > .001D) entity.setVelocity(new Vector(-direction.getZ(), 0.0, direction.getX()).normalize().multiply(speed));
    }

    static LivingEntity woundedAlly(LivingEntity source, double range) {
        return source.getNearbyEntities(range, range, range).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .filter(entity -> entity != source && !entity.isDead() && !(entity instanceof Player))
                .filter(entity -> entity.getHealth() < entity.getMaxHealth())
                .min(Comparator.comparingDouble(entity -> entity.getHealth() / Math.max(1.0, entity.getMaxHealth())))
                .orElse(null);
    }

    static List<LivingEntity> nearbyAllies(LivingEntity source, double range) {
        return source.getNearbyEntities(range, range, range).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .filter(entity -> entity != source && !entity.isDead() && !(entity instanceof Player))
                .toList();
    }

    static void playerTarget(LivingEntity entity, Player target) {
        if (entity instanceof Mob mob) mob.setTarget(target);
    }
}
