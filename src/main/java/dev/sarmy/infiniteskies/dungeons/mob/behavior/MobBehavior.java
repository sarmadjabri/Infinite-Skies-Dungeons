package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;

/**
 * Owns the creature-specific combat behavior for one custom mob type.
 * Lifecycle and persistent identity remain in the mob manager.
 */
public interface MobBehavior {
    String mobId();

    CustomMobDefinition definition();
}

