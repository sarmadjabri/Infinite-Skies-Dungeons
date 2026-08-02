package dev.sarmy.infiniteskies.dungeons.listener;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobManager;
import dev.sarmy.infiniteskies.dungeons.combat.ControlledDamage;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.SkyReaverBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.CrumblingHuskBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RuinCrawlerBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.MossboundBruteBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RiftArcherBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.CeilingStalkerBehavior;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Enforces approved direct-hit cooldowns for vanilla-controller attacks. */
public final class CustomMobCombatListener implements Listener {
    private final CustomMobManager mobs;
    private final Map<UUID, Long> nextSkyReaverHitMillis = new HashMap<>();
    private final Map<UUID, Long> nextMeleeHitMillis = new HashMap<>();
    private final Map<UUID, Long> nextConfiguredMeleeMillis = new HashMap<>();

    public CustomMobCombatListener(CustomMobManager mobs) { this.mobs = mobs; }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (ControlledDamage.isApplying()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        var instance = mobs.instance(attacker.getUniqueId());
        if (instance.isPresent() && isControlledCaster(instance.get().definition().id())) {
            event.setCancelled(true);
            return;
        }
        boolean crumblingHusk = mobs.mobId(attacker).map(CrumblingHuskBehavior.ID::equals).orElse(false);
        if (crumblingHusk && !(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        if (crumblingHusk && CrumblingHuskBehavior.hasThrownSword(attacker)) {
            event.setCancelled(true);
            return;
        }
        if (crumblingHusk && event.getEntity() instanceof Player) {
            long now = System.currentTimeMillis();
            long readyAt = nextMeleeHitMillis.getOrDefault(attacker.getUniqueId(), 0L);
            if (now < readyAt) { event.setCancelled(true); return; }
            event.setDamage(CrumblingHuskBehavior.MELEE_DAMAGE);
            nextMeleeHitMillis.put(attacker.getUniqueId(), now + CrumblingHuskBehavior.ATTACK_COOLDOWN_TICKS * 50L);
            return;
        }
        if (mobs.mobId(attacker).map(MossboundBruteBehavior.ID::equals).orElse(false)
                && !(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        boolean ruinCrawler = mobs.mobId(attacker).map(RuinCrawlerBehavior.ID::equals).orElse(false);
        if (ruinCrawler && !(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            return;
        }
        if (ruinCrawler && event.getEntity() instanceof Player player) {
            event.setDamage(4.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true, true, true));
            RuinCrawlerBehavior.beginRetreat(attacker);
            return;
        }
        if (instance.isPresent() && isConfiguredMelee(instance.get().definition().id())) {
            if (!(event.getEntity() instanceof Player)) { event.setCancelled(true); return; }
            long now = System.currentTimeMillis();
            if (now < nextConfiguredMeleeMillis.getOrDefault(attacker.getUniqueId(), 0L)) {
                event.setCancelled(true); return;
            }
            double damage = instance.get().definition().attackDamage();
            if (instance.get().definition().id().equals(CeilingStalkerBehavior.ID)
                    && attacker.getScoreboardTags().remove(CeilingStalkerBehavior.AMBUSH_TAG)) damage = CeilingStalkerBehavior.AMBUSH_DAMAGE;
            event.setDamage(damage);
            nextConfiguredMeleeMillis.put(attacker.getUniqueId(), now + meleeCooldownMillis(instance.get().definition().id()));
            return;
        }
        if (instance.isPresent() && instance.get().definition().id().equals("ruin_colossus")
                && event.getCause().name().equals("SONIC_BOOM")) { event.setCancelled(true); return; }
        if (instance.isEmpty() || !instance.get().definition().id().equals(SkyReaverBehavior.ID)) return;
        long now = System.currentTimeMillis();
        long readyAt = nextSkyReaverHitMillis.getOrDefault(attacker.getUniqueId(), 0L);
        if (now < readyAt) { event.setCancelled(true); return; }
        event.setDamage(SkyReaverBehavior.SWOOP_DAMAGE);
        nextSkyReaverHitMillis.put(attacker.getUniqueId(), now + SkyReaverBehavior.SWOOP_COOLDOWN_TICKS * 50L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity attacker)) return;
        if (mobs.isManaged(attacker) && !(event.getTarget() instanceof Player)) event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        var instance = mobs.instance(event.getEntity().getUniqueId());
        if (instance.isPresent()) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
        if (instance.isPresent() && instance.get().definition().id().equals(CrumblingHuskBehavior.ID)) {
            event.getEntity().getWorld().spawnParticle(Particle.BLOCK, event.getEntity().getLocation().add(0, .6, 0),
                    40, .45, .45, .45, Material.STONE.createBlockData());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof LivingEntity entity
                && (mobs.mobId(entity).map("shard_slinger"::equals).orElse(false)
                || mobs.mobId(entity).map(RiftArcherBehavior.ID::equals).orElse(false)
                || mobs.mobId(entity).map("ruin_standard_bearer"::equals).orElse(false))) {
            event.setCancelled(true);
        }
    }

    private boolean isConfiguredMelee(String id) {
        return switch (id) {
            case "ruin_standard_bearer", "ceiling_stalker", "mimic_husk", "buried_guardian", "false_statue" -> true;
            case "dust_wretch" -> true;
            default -> false;
        };
    }

    private boolean isControlledCaster(String id) {
        return switch (id) {
            case "observatory_watcher", "venom_spitter", "gale_acolyte", "moss_priest", "void_mender",
                    "grave_chanter", "stone_binder", "ember_wisp", "storm_wisp", "frost_wisp", "dust_phantom", "rift_mine" -> true;
            default -> false;
        };
    }

    private long meleeCooldownMillis(String id) {
        return switch (id) {
            case "dust_wretch" -> 0L;
            case "buried_guardian", "false_statue" -> 2500L;
            case "ceiling_stalker" -> 2000L;
            default -> 2000L;
        };
    }
}
