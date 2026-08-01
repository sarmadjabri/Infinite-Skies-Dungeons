package dev.sarmy.infiniteskies.dungeons.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashSet;
import java.util.Set;

/** Runs all active dungeon combat work from one Bukkit task. */
public final class CentralTickService {
    private final Plugin plugin;
    private final Set<Tickable> tickables = new LinkedHashSet<>();
    private BukkitTask task;
    private long tick;

    public CentralTickService(Plugin plugin) { this.plugin = plugin; }
    public void register(Tickable tickable) { tickables.add(tickable); }
    public void unregister(Tickable tickable) { tickables.remove(tickable); }
    public void start() { if (task == null) task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L); }
    public void stop() { if (task != null) task.cancel(); task = null; tickables.clear(); }
    public long currentTick() { return tick; }
    private void tick() { tick++; for (Tickable tickable : Set.copyOf(tickables)) tickable.tick(tick); }
}
