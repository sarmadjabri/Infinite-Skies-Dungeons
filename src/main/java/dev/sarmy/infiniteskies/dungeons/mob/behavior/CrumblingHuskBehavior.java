package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

/** Dedicated combat holder for the Crumbling Husk. */
public final class CrumblingHuskBehavior implements MobBehavior {
    public static final String ID = "crumbling_husk";
    public static final double HEALTH = 50.0;
    public static final double MELEE_DAMAGE = 5.0;
    public static final int ATTACK_COOLDOWN_TICKS = 60;
    // Vanilla Husk/Zombie base movement speed is 0.23; this is 30% slower.
    public static final double MOVEMENT_SPEED = 0.161;
    public static final double HIDDEN_ARMOR = 14.0;
    public static final double HIDDEN_ARMOR_TOUGHNESS = 6.0;
    public static final int LUNGE_COOLDOWN_TICKS = 300;
    private static final double LUNGE_SPEED = 0.85;
    private final Map<UUID, Long> nextLungeTick = new HashMap<>();

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Crumbling Husk", EntityType.HUSK,
                MobRank.NORMAL, HEALTH, MELEE_DAMAGE, MOVEMENT_SPEED);
    }

    @Override
    public void applyEquipment(LivingEntity entity) {
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
}
