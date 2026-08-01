package dev.sarmy.infiniteskies.dungeons.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/** Namespaced persistent-data keys used to identify managed entities. */
public final class PersistentKeys {
    private final NamespacedKey dataVersion;
    private final NamespacedKey mobId;
    private final NamespacedKey instanceId;
    private final NamespacedKey mobRank;
    private final NamespacedKey spawnEggMobId;
    private final NamespacedKey spawnerMobId;

    public PersistentKeys(Plugin plugin) {
        dataVersion = new NamespacedKey(plugin, "data_version");
        mobId = new NamespacedKey(plugin, "mob_id");
        instanceId = new NamespacedKey(plugin, "instance_id");
        mobRank = new NamespacedKey(plugin, "mob_rank");
        spawnEggMobId = new NamespacedKey(plugin, "spawn_egg_mob_id");
        spawnerMobId = new NamespacedKey(plugin, "spawner_mob_id");
    }

    public NamespacedKey dataVersion() { return dataVersion; }
    public NamespacedKey mobId() { return mobId; }
    public NamespacedKey instanceId() { return instanceId; }
    public NamespacedKey mobRank() { return mobRank; }
    public NamespacedKey spawnEggMobId() { return spawnEggMobId; }
    public NamespacedKey spawnerMobId() { return spawnerMobId; }
}
