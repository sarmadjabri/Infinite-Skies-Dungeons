package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Fast side-flanking Cave Spider with a bounded leap and short retreat. */
public final class RuinCrawlerBehavior implements MobBehavior {
    public static final String ID = "ruin_crawler";
    public static final double HEALTH = 5.0;
    public static final double HIDDEN_ARMOR = 14.0;
    public static final double HIDDEN_ARMOR_TOUGHNESS = 6.0;
    public static final int LEAP_COOLDOWN_TICKS = 140;
    private static final long RETREAT_MILLIS = 750L;
    private static final Map<UUID, Long> retreatUntilMillis = new HashMap<>();

    private final Map<UUID, Long> nextLeapTick = new HashMap<>();
    private final Map<UUID, Integer> flankSide = new HashMap<>();

    @Override public String mobId() { return ID; }

    @Override
    public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Ruin Crawler", EntityType.CAVE_SPIDER,
                MobRank.NORMAL, HEALTH, 4.0, .3);
    }

    @Override
    public void applyEquipment(LivingEntity entity) {
        // Protection is deliberately attribute-only: no armor becomes visible.
        if (entity.getAttribute(Attribute.ARMOR) != null) {
            entity.getAttribute(Attribute.ARMOR).setBaseValue(HIDDEN_ARMOR);
        }
        if (entity.getAttribute(Attribute.ARMOR_TOUGHNESS) != null) {
            entity.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(HIDDEN_ARMOR_TOUGHNESS);
        }
    }

    @Override
    public void tick(LivingEntity crawler, long tick) {
        Player target = CombatTargets.nearestPlayer(crawler, 20.0);
        if (target == null) return;

        Vector towardPlayer = horizontalDirection(crawler, target);
        if (towardPlayer == null) return;
        if (retreatUntilMillis.getOrDefault(crawler.getUniqueId(), 0L) > System.currentTimeMillis()) {
            move(crawler, towardPlayer.multiply(-.30D));
            return;
        }

        double distanceSquared = crawler.getLocation().distanceSquared(target.getLocation());
        if (distanceSquared >= 6.25D && distanceSquared <= 144.0D
                && nextLeapTick.getOrDefault(crawler.getUniqueId(), 0L) <= tick) {
            crawler.setVelocity(towardPlayer.multiply(.72D).setY(.32D));
            crawler.getWorld().spawnParticle(Particle.ASH, crawler.getLocation().add(0, .2, 0), 12, .2, .08, .2, .02);
            crawler.getWorld().playSound(crawler.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, .7F, 1.6F);
            nextLeapTick.put(crawler.getUniqueId(), tick + LEAP_COOLDOWN_TICKS);
            return;
        }

        // Head for a persistent side point instead of running straight at the player.
        int side = flankSide.computeIfAbsent(crawler.getUniqueId(), ignored -> (crawler.getUniqueId().getLeastSignificantBits() & 1L) == 0L ? 1 : -1);
        Vector sideways = new Vector(-towardPlayer.getZ(), 0, towardPlayer.getX()).multiply(side);
        Vector flankMovement = towardPlayer.multiply(.35D).add(sideways.multiply(.65D)).normalize().multiply(.20D);
        move(crawler, flankMovement);
    }

    /** Called only after this crawler lands a player hit. */
    public static void beginRetreat(LivingEntity crawler) {
        retreatUntilMillis.put(crawler.getUniqueId(), System.currentTimeMillis() + RETREAT_MILLIS);
    }

    private Vector horizontalDirection(LivingEntity crawler, Player target) {
        Vector direction = target.getLocation().toVector().subtract(crawler.getLocation().toVector());
        direction.setY(0);
        return direction.lengthSquared() == 0 ? null : direction.normalize();
    }

    private void move(LivingEntity entity, Vector horizontalVelocity) {
        entity.setVelocity(horizontalVelocity.setY(entity.getVelocity().getY()));
    }
}
