package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/** Dedicated combat holder for the Crumbling Husk. */
public final class CrumblingHuskBehavior implements MobBehavior {
    public static final String ID = "crumbling_husk";
    public static final double HEALTH = 50.0;
    public static final double MELEE_DAMAGE = 12.0;
    public static final int ATTACK_COOLDOWN_TICKS = 60;
    // Vanilla Husk/Zombie base movement speed is 0.23; this is 30% slower.
    public static final double MOVEMENT_SPEED = 0.161;
    public static final double HIDDEN_ARMOR = 14.0;
    public static final double HIDDEN_ARMOR_TOUGHNESS = 6.0;
    public static final int LUNGE_COOLDOWN_TICKS = 300;
    public static final double SWORD_THROW_DAMAGE = 12.0;
    private static final String SWORD_THROWN_TAG = "isdungeon_crumbling_sword_thrown";
    private static final int SWORD_PROJECTILE_LIFETIME_TICKS = 50;
    private static final double LUNGE_SPEED = 0.85;
    private final Map<UUID, Long> nextLungeTick = new HashMap<>();
    private final Map<UUID, FlyingSword> flyingSwords = new HashMap<>();
    private long lastProjectileTick = Long.MIN_VALUE;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Crumbling Husk", EntityType.HUSK,
                MobRank.NORMAL, HEALTH, MELEE_DAMAGE, MOVEMENT_SPEED);
    }

    @Override
    public void applyEquipment(LivingEntity entity) {
        if (hasThrownSword(entity)) {
            entity.getEquipment().setItemInMainHand(ItemStack.empty());
            entity.getEquipment().setItemInMainHandDropChance(0.0F);
            entity.setAI(false);
            entity.setVelocity(new Vector());
            return;
        }
        ItemStack sword = ItemStack.of(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        sword.setItemMeta(meta);
        entity.getEquipment().setItemInMainHand(sword);
        entity.getEquipment().setItemInMainHandDropChance(0.0F);
        // Attribute-based protection intentionally has no visible armor pieces.
        if (entity.getAttribute(Attribute.ARMOR) != null) {
            entity.getAttribute(Attribute.ARMOR).setBaseValue(HIDDEN_ARMOR);
        }
        if (entity.getAttribute(Attribute.ARMOR_TOUGHNESS) != null) {
            entity.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(HIDDEN_ARMOR_TOUGHNESS);
        }
    }

    @Override
    public void tick(LivingEntity entity, long tick) {
        tickFlyingSwords(tick);
        if (!hasThrownSword(entity) && entity.getHealth() <= entity.getMaxHealth() * .10D) {
            Player target = CombatTargets.nearestPlayer(entity, 20.0);
            if (target != null) throwSword(entity, tick);
        }
        if (hasThrownSword(entity)) return;
        Player target = CombatTargets.nearestPlayer(entity, 20.0);
        if (target == null || nextLungeTick.getOrDefault(entity.getUniqueId(), 0L) > tick) return;

        double distanceSquared = entity.getLocation().distanceSquared(target.getLocation());
        // Do not waste the burst when already in sword range or when the player
        // is far outside the encounter.
        if (distanceSquared < 6.25 || distanceSquared > 324.0) return;

        Vector burst = target.getLocation().toVector().subtract(entity.getLocation().toVector());
        burst.setY(0.0);
        if (burst.lengthSquared() == 0.0) return;
        entity.setVelocity(burst.normalize().multiply(LUNGE_SPEED).setY(0.12));
        entity.getWorld().spawnParticle(Particle.BLOCK, entity.getLocation().add(0, .15, 0),
                24, .28, .08, .28, Material.STONE.createBlockData());
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_HUSK_STEP, 1F, .65F);
        nextLungeTick.put(entity.getUniqueId(), tick + LUNGE_COOLDOWN_TICKS);
    }

    public static boolean hasThrownSword(LivingEntity entity) {
        return entity.getScoreboardTags().contains(SWORD_THROWN_TAG);
    }

    private void throwSword(LivingEntity husk, long tick) {
        Location origin = husk.getLocation().add(0, 1.35, 0);
        ItemStack sword = husk.getEquipment().getItemInMainHand().clone();
        if (sword.getType().isAir()) sword = ItemStack.of(Material.IRON_SWORD);
        Item projectile = husk.getWorld().dropItem(origin, sword);
        projectile.setPickupDelay(Integer.MAX_VALUE);
        projectile.setGravity(false);
        Vector direction = origin.getDirection().normalize();
        direction.setY(direction.getY() - .05D);
        projectile.setVelocity(direction.normalize().multiply(1.15D));
        husk.getEquipment().setItemInMainHand(ItemStack.empty());
        husk.addScoreboardTag(SWORD_THROWN_TAG);
        husk.setAI(false);
        husk.setVelocity(new Vector());
        flyingSwords.put(projectile.getUniqueId(), new FlyingSword(projectile, husk.getUniqueId(), tick + SWORD_PROJECTILE_LIFETIME_TICKS, origin.clone()));
        husk.getWorld().spawnParticle(Particle.BLOCK, origin, 20, .25, .25, .25, Material.STONE.createBlockData());
        husk.getWorld().playSound(origin, Sound.ENTITY_DROWNED_SHOOT, 1F, .85F);
    }

    private void tickFlyingSwords(long tick) {
        if (lastProjectileTick == tick) return;
        lastProjectileTick = tick;
        for (FlyingSword sword : List.copyOf(flyingSwords.values())) {
            Item projectile = sword.projectile();
            if (!projectile.isValid() || tick >= sword.expireTick() || !projectile.getLocation().getBlock().isPassable()) {
                projectile.remove();
                flyingSwords.remove(projectile.getUniqueId());
                continue;
            }
            projectile.getWorld().spawnParticle(Particle.BLOCK, projectile.getLocation(), 12, .18, .18, .18,
                    Material.STONE.createBlockData());
            Location currentLocation = projectile.getLocation();
            for (Player player : projectile.getWorld().getPlayers()) {
                if (player.isDead() || !intersectsPath(sword.previousLocation(), currentLocation, player)) continue;
                flyingSwords.remove(projectile.getUniqueId());
                var owner = Bukkit.getEntity(sword.ownerId());
                ControlledDamage.apply(owner == null ? projectile : owner, player, SWORD_THROW_DAMAGE);
                projectile.getWorld().spawnParticle(Particle.BLOCK, projectile.getLocation(), 24, .25, .25, .25,
                        Material.STONE.createBlockData());
                projectile.remove();
                break;
            }
            if (projectile.isValid()) {
                flyingSwords.put(projectile.getUniqueId(), new FlyingSword(projectile, sword.ownerId(), sword.expireTick(), currentLocation.clone()));
            }
        }
    }

    /** Checks the full movement path, preventing a fast item from skipping through a player between server ticks. */
    private boolean intersectsPath(Location from, Location to, Player player) {
        Vector movement = to.toVector().subtract(from.toVector());
        int steps = Math.max(1, (int) Math.ceil(movement.length() / .12D));
        for (int step = 0; step <= steps; step++) {
            Vector point = from.toVector().add(movement.clone().multiply(step / (double) steps));
            if (player.getBoundingBox().expand(.45D).contains(point)) return true;
        }
        return false;
    }

    private record FlyingSword(Item projectile, UUID ownerId, long expireTick, Location previousLocation) { }
}
