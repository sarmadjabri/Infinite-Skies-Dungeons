package dev.sarmy.infiniteskies.dungeons.listener;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobManager;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.SkyReaverBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.CrumblingHuskBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RuinCrawlerBehavior;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Enforces approved direct-hit cooldowns for vanilla-controller attacks. */
public final class CustomMobCombatListener implements Listener {
    private final CustomMobManager mobs;
    private final Map<UUID, Long> nextSkyReaverHitMillis = new HashMap<>();
    private final Map<UUID, Long> nextMeleeHitMillis = new HashMap<>();

    public CustomMobCombatListener(CustomMobManager mobs) { this.mobs = mobs; }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        var instance = mobs.instance(attacker.getUniqueId());
        if (instance.isPresent() && instance.get().definition().id().equals(RuinCrawlerBehavior.ID)
                && event.getEntity() instanceof Player player) {
            event.setDamage(4.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true, true, true));
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
}
