package dev.sarmy.infiniteskies.dungeons.combat;

import dev.sarmy.infiniteskies.dungeons.scheduler.Tickable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/** Tracks bounded plugin-owned projectile damage and expiry from the central tick loop. */
public final class ProjectileService implements Tickable {
    private final Map<UUID, ProjectileData> projectiles = new HashMap<>();

    public void launch(LivingEntity source, Location target, Material visual, double speed, double damage, long tick, long lifetimeTicks) {
        launch(source, target, visual, speed, damage, tick, lifetimeTicks, (projectile, player) -> { });
    }
    public void launch(LivingEntity source, Location target, Material visual, double speed, double damage, long tick, long lifetimeTicks, BiConsumer<Snowball, Player> impact) {
        Location origin = source.getLocation().add(0, source.getHeight() * .65, 0);
        Snowball projectile = source.getWorld().spawn(origin, Snowball.class);
        projectile.setItem(ItemStack.of(visual));
        projectile.setGravity(false);
        projectile.setShooter(source);
        projectile.setVelocity(target.toVector().subtract(origin.toVector()).normalize().multiply(speed));
        projectiles.put(projectile.getUniqueId(), new ProjectileData(projectile, damage, tick + lifetimeTicks, impact));
    }

    @Override
    public void tick(long tick) {
        for (ProjectileData data : List.copyOf(projectiles.values())) {
            Snowball projectile = data.projectile();
            if (!projectile.isValid() || tick >= data.expiry() || !projectile.getLocation().getBlock().isPassable()) {
                remove(projectile); continue;
            }
            for (Player player : projectile.getWorld().getPlayers()) if (CombatTargets.valid(player)
                    && player.getLocation().distanceSquared(projectile.getLocation()) <= 1.44) {
                ControlledDamage.apply(projectile, player, data.damage());
                data.impact().accept(projectile, player);
                projectile.getWorld().spawnParticle(Particle.CRIT, projectile.getLocation(), 8, .15, .15, .15, .03);
                remove(projectile); break;
            }
        }
    }

    private void remove(Snowball projectile) { projectiles.remove(projectile.getUniqueId()); projectile.remove(); }
    private record ProjectileData(Snowball projectile, double damage, long expiry, BiConsumer<Snowball, Player> impact) { }
}
