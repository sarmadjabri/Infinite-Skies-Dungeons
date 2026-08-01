package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Live basic and heavy-shot behavior for the Shard Slinger. */
public final class ShardSlingerBehavior implements MobBehavior {
    public static final String ID = "shard_slinger";
    public static final double HEALTH = 50;
    public static final double BASIC_DAMAGE = 5;
    public static final double HEAVY_DAMAGE = 15;
    public static final int BASIC_COOLDOWN_TICKS = 100;
    public static final int HEAVY_COOLDOWN_TICKS = 200;
    public static final int HEAVY_TELEGRAPH_TICKS = 20;
    private final ProjectileService projectiles;
    private final Map<UUID, Long> nextBasic = new HashMap<>();
    private final Map<UUID, Long> nextHeavy = new HashMap<>();
    private final Map<UUID, HeavyCast> heavyCasts = new HashMap<>();

    public ShardSlingerBehavior(ProjectileService projectiles) { this.projectiles = projectiles; }
    @Override public String mobId() { return ID; }
    @Override public CustomMobDefinition definition() { return new CustomMobDefinition(ID, "Shard Slinger", EntityType.SKELETON, MobRank.NORMAL, HEALTH, 0, .25); }

    @Override public void applyEquipment(LivingEntity entity) {
        ItemStack bow = ItemStack.of(Material.BOW);
        ItemMeta meta = bow.getItemMeta(); meta.setUnbreakable(true); bow.setItemMeta(meta);
        entity.getEquipment().setItemInMainHand(bow); entity.getEquipment().setItemInMainHandDropChance(0F);
    }

    @Override public void tick(LivingEntity slinger, long tick) {
        Player target = CombatTargets.nearestPlayer(slinger, 24);
        if (target == null) return;
        HeavyCast cast = heavyCasts.get(slinger.getUniqueId());
        if (cast != null) {
            Telegraphs.line(slinger.getWorld(), slinger.getLocation().add(0, 1.4, 0), cast.target(), Particle.END_ROD, 12);
            if (tick >= cast.fireTick()) { projectiles.launch(slinger, cast.target(), Material.AMETHYST_SHARD, 1.25, HEAVY_DAMAGE, tick, 60); heavyCasts.remove(slinger.getUniqueId()); }
            return;
        }
        if (nextHeavy.getOrDefault(slinger.getUniqueId(), 0L) <= tick) {
            heavyCasts.put(slinger.getUniqueId(), new HeavyCast(target.getLocation().clone().add(0, 1, 0), tick + HEAVY_TELEGRAPH_TICKS));
            nextHeavy.put(slinger.getUniqueId(), tick + HEAVY_COOLDOWN_TICKS);
            slinger.getWorld().playSound(slinger.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1F, .7F);
        } else if (nextBasic.getOrDefault(slinger.getUniqueId(), 0L) <= tick) {
            projectiles.launch(slinger, target.getLocation().clone().add(0, 1, 0), Material.ARROW, 1.1, BASIC_DAMAGE, tick, 60);
            nextBasic.put(slinger.getUniqueId(), tick + BASIC_COOLDOWN_TICKS);
        }
    }
    private record HeavyCast(Location target, long fireTick) { }
}
