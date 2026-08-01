package dev.sarmy.infiniteskies.dungeons.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/** Applies bounded plugin-owned damage without destructive vanilla side effects. */
public final class ControlledDamage {
    private ControlledDamage() { }
    public static void apply(Entity source, LivingEntity target, double damage) {
        if (damage > 0 && target.isValid() && !target.isDead()) target.damage(damage, source);
    }
}
