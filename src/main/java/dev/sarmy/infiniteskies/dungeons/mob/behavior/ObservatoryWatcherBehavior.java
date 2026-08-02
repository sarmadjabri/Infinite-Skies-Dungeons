package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.combat.ProjectileService;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import dev.sarmy.infiniteskies.dungeons.visual.Telegraphs;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Stationary long-range turret with a clear charged-shot warning. */
public final class ObservatoryWatcherBehavior implements MobBehavior {
    public static final String ID = "observatory_watcher";
    public static final double HEALTH = 40, CHARGED_SHOT_DAMAGE = 12;
    public static final int SHOT_COOLDOWN_TICKS = 160, TELEGRAPH_TICKS = 30;
    private final ProjectileService projectiles;
    private final Map<UUID, Cast> casts = new HashMap<>();
    private final Map<UUID, Long> nextShot = new HashMap<>();

    public ObservatoryWatcherBehavior(ProjectileService projectiles) { this.projectiles = projectiles; }
    public String mobId() { return ID; }
    public CustomMobDefinition definition() { return new CustomMobDefinition(ID, "Observatory Watcher", EntityType.SKELETON, MobRank.NORMAL, HEALTH, 0, .025); }
    @Override public void applyEquipment(LivingEntity entity) { entity.setAI(false); }
    @Override public void tick(LivingEntity watcher, long tick) {
        Player player = BehaviorSupport.target(watcher, 40);
        Cast cast = casts.get(watcher.getUniqueId());
        if (cast != null) {
            Telegraphs.line(watcher.getWorld(), watcher.getLocation().add(0, 1.5, 0), cast.point(), Particle.END_ROD, 16);
            if (tick >= cast.fireTick()) {
                projectiles.launch(watcher, cast.point(), Material.ARROW, 1.45, CHARGED_SHOT_DAMAGE, tick, 70);
                casts.remove(watcher.getUniqueId());
            }
            return;
        }
        if (player == null || nextShot.getOrDefault(watcher.getUniqueId(), 0L) > tick) return;
        BehaviorSupport.face(watcher, player.getEyeLocation());
        casts.put(watcher.getUniqueId(), new Cast(player.getEyeLocation().clone(), tick + TELEGRAPH_TICKS));
        nextShot.put(watcher.getUniqueId(), tick + SHOT_COOLDOWN_TICKS);
        watcher.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, watcher.getLocation().add(0, 1.4, 0), 12, .1, .1, .1, .02);
    }
    private record Cast(org.bukkit.Location point, long fireTick) { }
}
