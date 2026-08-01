package dev.sarmy.infiniteskies.dungeons.combat;

import org.bukkit.Location;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/** Bounded main-thread target selection shared by custom mob behaviors. */
public final class CombatTargets {
    private CombatTargets() { }

    public static Player nearestPlayer(LivingEntity source, double range) {
        Player nearest = null;
        double nearestSquared = range * range;
        for (Player player : source.getWorld().getPlayers()) {
            if (!valid(player)) continue;
            double distance = player.getLocation().distanceSquared(source.getLocation());
            if (distance < nearestSquared) { nearestSquared = distance; nearest = player; }
        }
        return nearest;
    }

    public static boolean valid(Player player) {
        return player.isOnline() && !player.isDead() && !player.isInvulnerable()
                && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE);
    }

    public static boolean within(Location location, Player player, double range) {
        return player.getWorld().equals(location.getWorld()) && player.getLocation().distanceSquared(location) <= range * range;
    }
}
