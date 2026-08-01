package dev.sarmy.infiniteskies.dungeons;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobManager;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobEggService;
import dev.sarmy.infiniteskies.dungeons.command.IsDungeonCommand;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.MossboundBruteBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RiftArcherBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RuinColossusBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.SkyReaverBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.StormCallerBehavior;
import dev.sarmy.infiniteskies.dungeons.listener.CustomMobSpawnListener;
import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap for the Infinite Skies dungeon encounter framework.
 */
public final class InfiniteSkiesDungeonsPlugin extends JavaPlugin {
    private CustomMobManager mobManager;
    private PersistentKeys persistentKeys;
    private CustomMobEggService eggService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        persistentKeys = new PersistentKeys(this);
        mobManager = new CustomMobManager(persistentKeys);
        mobManager.register(new MossboundBruteBehavior().definition());
        mobManager.register(new RiftArcherBehavior().definition());
        mobManager.register(new StormCallerBehavior().definition());
        mobManager.register(new SkyReaverBehavior().definition());
        mobManager.register(new RuinColossusBehavior().definition());
        eggService = new CustomMobEggService(persistentKeys);
        getServer().getPluginManager().registerEvents(
                new CustomMobSpawnListener(mobManager, eggService, persistentKeys), this);
        IsDungeonCommand command = new IsDungeonCommand(mobManager, eggService);
        getCommand("isdungeon").setExecutor(command);
        getCommand("isdungeon").setTabCompleter(command);
        getLogger().info(() -> "InfiniteSkiesDungeons " + getPluginMeta().getVersion() + " enabled.");
    }

    public CustomMobManager mobManager() {
        return mobManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("InfiniteSkiesDungeons disabled.");
    }
}
