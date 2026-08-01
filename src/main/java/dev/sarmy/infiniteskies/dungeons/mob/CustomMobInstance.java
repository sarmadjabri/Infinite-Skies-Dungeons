package dev.sarmy.infiniteskies.dungeons.mob;

import java.util.UUID;

/** Runtime identity of one managed vanilla controller entity. */
public record CustomMobInstance(UUID entityId, UUID instanceId, CustomMobDefinition definition) { }

