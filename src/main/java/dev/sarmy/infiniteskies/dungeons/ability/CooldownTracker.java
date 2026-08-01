package dev.sarmy.infiniteskies.dungeons.ability;

import java.util.HashMap;
import java.util.Map;

/** Stores cooldown expiry ticks without one scheduled task per ability. */
public final class CooldownTracker {
    private final Map<String, Long> expiryTicks = new HashMap<>();
    public boolean ready(String abilityId, long tick) { return expiryTicks.getOrDefault(abilityId, 0L) <= tick; }
    public void trigger(String abilityId, long tick, long durationTicks) { expiryTicks.put(abilityId, tick + durationTicks); }
    public long remaining(String abilityId, long tick) { return Math.max(0L, expiryTicks.getOrDefault(abilityId, 0L) - tick); }
    public void clear() { expiryTicks.clear(); }
}
