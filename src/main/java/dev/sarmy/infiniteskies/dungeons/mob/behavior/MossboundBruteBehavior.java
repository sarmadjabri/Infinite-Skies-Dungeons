package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Live combat behavior for the Mossbound Brute. */
public final class MossboundBruteBehavior implements MobBehavior {
    public static final String ID = "mossbound_brute";
    public static final double HEALTH = 100.0;
    public static final double MELEE_DAMAGE = 17.0;
    public static final double BLOCK_THROW_DAMAGE = 20.0;
    public static final int BLOCK_THROW_COOLDOWN_TICKS = 100;
    private static final int PROJECTILE_LIFETIME_TICKS = 60;
    private final Map<UUID, Long> nextThrowTick = new HashMap<>();
    private final Map<UUID, FlyingBlock> flyingBlocks = new HashMap<>();

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Mossbound Brute", EntityType.IRON_GOLEM,
                MobRank.NORMAL, HEALTH, MELEE_DAMAGE, 0.25);
    }

    @Override
    public void tick(LivingEntity entity, long tick) {
        tickFlyingBlocks(tick);
        if (!(entity instanceof IronGolem golem)) return;
        Player target = nearestPlayer(golem, 20.0);
        if (target == null) return;
        golem.setTarget(target); // Prevent vanilla hostile-mob prioritisation.

        if (nextThrowTick.getOrDefault(golem.getUniqueId(), 0L) > tick
                || golem.getLocation().distanceSquared(target.getLocation()) < 25.0) return;
        nextThrowTick.put(golem.getUniqueId(), tick + BLOCK_THROW_COOLDOWN_TICKS);
        launchBlock(golem, target, tick);
    }

    private void launchBlock(IronGolem golem, Player target, long tick) {
        Location origin = golem.getLocation().add(0, 1.8, 0);
        Item projectile = golem.getWorld().dropItem(origin, ItemStack.of(Material.MOSS_BLOCK));
        projectile.setPickupDelay(Integer.MAX_VALUE);
        projectile.setGravity(false);
        projectile.setVelocity(target.getEyeLocation().toVector().subtract(origin.toVector()).normalize().multiply(1.05));
        flyingBlocks.put(projectile.getUniqueId(), new FlyingBlock(projectile, target.getUniqueId(), tick + PROJECTILE_LIFETIME_TICKS));
        golem.getWorld().playSound(origin, Sound.ENTITY_SNOWBALL_THROW, 1F, .65F);
    }

    private void tickFlyingBlocks(long tick) {
        for (FlyingBlock block : List.copyOf(flyingBlocks.values())) {
            Item projectile = block.projectile();
            if (!projectile.isValid() || tick >= block.expireTick() || !projectile.getLocation().getBlock().isPassable()) { projectile.remove(); flyingBlocks.remove(projectile.getUniqueId()); continue; }
            projectile.getWorld().spawnParticle(Particle.BLOCK, projectile.getLocation(), 80, .6, .6, .6, Material.MOSS_BLOCK.createBlockData());
            for (Player player : projectile.getWorld().getPlayers()) if (!player.isDead() && player.getLocation().distanceSquared(projectile.getLocation()) <= 2.25) {
                ControlledDamage.apply(projectile, player, BLOCK_THROW_DAMAGE);
                player.setVelocity(player.getLocation().toVector().subtract(projectile.getLocation().toVector()).normalize().multiply(.65).setY(.22));
                projectile.getWorld().spawnParticle(Particle.BLOCK, projectile.getLocation(), 20, .3, .3, .3, Material.MOSS_BLOCK.createBlockData());
                projectile.remove(); flyingBlocks.remove(projectile.getUniqueId()); break;
            }
        }
    }

    private Player nearestPlayer(IronGolem golem, double range) {
        Player result = null; double closest = range * range;
        for (Player player : golem.getWorld().getPlayers()) if (!player.isDead() && !player.isInvulnerable()) {
            double distance = player.getLocation().distanceSquared(golem.getLocation());
            if (distance < closest) { closest = distance; result = player; }
        }
        return result;
    }

    private record FlyingBlock(Item projectile, UUID targetId, long expireTick) { }
}
