package dev.sarmy.infiniteskies.dungeons.mob;

import org.bukkit.entity.EntityType;

import java.util.Objects;

/** Immutable, configuration-derived description of a custom mob. */
public record CustomMobDefinition(
        String id,
        String displayName,
        EntityType entityType,
        MobRank rank,
        double maximumHealth,
        double attackDamage,
        double movementSpeed
) {
    public CustomMobDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(displayName, "displayName");
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(rank, "rank");
        if (maximumHealth <= 0 || attackDamage < 0 || movementSpeed < 0) {
            throw new IllegalArgumentException("Mob attributes cannot be negative and health must be positive.");
        }
    }
}

