package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import org.bukkit.entity.LivingEntity;

/**
 * Owns the creature-specific combat behavior for one custom mob type.
 * Lifecycle and persistent identity remain in the mob manager.
 */
public interface MobBehavior {
    String mobId();

    CustomMobDefinition definition();

    /** Applies this mob's non-droppable vanilla equipment after it is spawned. */
    default void applyEquipment(LivingEntity entity) { }

    /** Executes this mob's stateful combat logic from the shared tick loop. */
    default void tick(LivingEntity entity, long tick) { }
}
