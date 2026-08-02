package dev.sarmy.infiniteskies.dungeons.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/** Applies bounded plugin-owned damage without destructive vanilla side effects. */
public final class ControlledDamage {
    private static final ThreadLocal<Boolean> APPLYING = ThreadLocal.withInitial(() -> false);
    private ControlledDamage() { }
    public static void apply(Entity source, LivingEntity target, double damage) {
        if (damage <= 0 || !target.isValid() || target.isDead()) return;
        APPLYING.set(true);
        try {
            target.damage(damage, source);
        } finally {
            APPLYING.remove();
        }
    }

    /** True only while a synchronous plugin-owned damage event is being emitted. */
    public static boolean isApplying() { return APPLYING.get(); }
}
