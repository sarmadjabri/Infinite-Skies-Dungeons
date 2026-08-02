package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Stray archer that visibly uses a bow but fires native Breeze wind charges. */
public final class RiftArcherBehavior implements MobBehavior {
    public static final String ID = "rift_archer";
    public static final double HEALTH = 40.0;
    // 0.25 is the normal Stray base movement speed.
    public static final double MOVEMENT_SPEED = 0.25;
    public static final double HIDDEN_ARMOR = 14.0;
    public static final double HIDDEN_ARMOR_TOUGHNESS = 6.0;
    public static final int SHOT_COOLDOWN_TICKS = 60;
    public static final int ESCAPE_COOLDOWN_TICKS = 100;
    private static final int DRAW_TICKS = 12;

    private final Map<UUID, Long> nextShotTick = new HashMap<>();
    private final Map<UUID, Long> nextEscapeTick = new HashMap<>();
    private final Map<UUID, Cast> casts = new HashMap<>();

    @Override public String mobId() { return ID; }

    @Override
    public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Rift Archer", EntityType.STRAY,
                MobRank.NORMAL, HEALTH, 0.0, MOVEMENT_SPEED);
    }

    @Override
    public void applyEquipment(LivingEntity entity) {
        ItemStack bow = ItemStack.of(org.bukkit.Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setUnbreakable(true);
        bow.setItemMeta(meta);
        entity.getEquipment().setItemInMainHand(bow);
        entity.getEquipment().setItemInMainHandDropChance(0.0F);
        if (entity.getAttribute(Attribute.ARMOR) != null) {
            entity.getAttribute(Attribute.ARMOR).setBaseValue(HIDDEN_ARMOR);
        }
        if (entity.getAttribute(Attribute.ARMOR_TOUGHNESS) != null) {
            entity.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(HIDDEN_ARMOR_TOUGHNESS);
        }
    }

    @Override
    public void tick(LivingEntity archer, long tick) {
        Player target = CombatTargets.nearestPlayer(archer, 20.0);
        if (target == null) return;

        attemptLowHealthEscape(archer, tick);
        Cast cast = casts.get(archer.getUniqueId());
        if (cast != null) {
            Telegraphs.line(archer.getWorld(), archer.getLocation().add(0, 1.4, 0), cast.target(), Particle.CLOUD, 8);
            if (tick >= cast.fireTick()) {
                launchNativeWindCharge(archer, cast.target());
                casts.remove(archer.getUniqueId());
            }
            return;
        }

        if (nextShotTick.getOrDefault(archer.getUniqueId(), 0L) <= tick) {
            casts.put(archer.getUniqueId(), new Cast(target.getLocation().clone().add(0, 1.0, 0), tick + DRAW_TICKS));
            nextShotTick.put(archer.getUniqueId(), tick + SHOT_COOLDOWN_TICKS);
            archer.getWorld().playSound(archer.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 1F, .9F);
        }
    }

    private void launchNativeWindCharge(LivingEntity archer, Location target) {
        Location origin = archer.getLocation().add(0, archer.getHeight() * .72D, 0);
        BreezeWindCharge charge = archer.getWorld().spawn(origin, BreezeWindCharge.class);
        charge.setShooter(archer);
        charge.setGravity(false);
        charge.setIsIncendiary(false);
        charge.setYield(0.0F);
        Vector trajectory = target.toVector().subtract(origin.toVector());
        if (trajectory.lengthSquared() > 0) charge.setVelocity(trajectory.normalize().multiply(1.15D));
        archer.getWorld().playSound(origin, Sound.ENTITY_BREEZE_SHOOT, 1F, 1F);
    }

    private void attemptLowHealthEscape(LivingEntity archer, long tick) {
        if (archer.getHealth() > archer.getMaxHealth() * .10D
                || nextEscapeTick.getOrDefault(archer.getUniqueId(), 0L) > tick) return;
        int direction = ThreadLocalRandom.current().nextBoolean() ? 3 : -3;
        Location destination = archer.getLocation().clone();
        if (ThreadLocalRandom.current().nextBoolean()) destination.add(direction, 0, 0);
        else destination.add(0, 0, direction);
        if (destination.getBlock().isPassable() && destination.clone().add(0, 1, 0).getBlock().isPassable()
                && !destination.clone().add(0, -1, 0).getBlock().isPassable()) {
            archer.teleport(destination);
        }
        nextEscapeTick.put(archer.getUniqueId(), tick + ESCAPE_COOLDOWN_TICKS);
    }

    private record Cast(Location target, long fireTick) { }
}
