package dev.sarmy.infiniteskies.dungeons;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap for the Infinite Skies dungeon encounter framework.
 */
public final class InfiniteSkiesDungeonsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info(() -> "InfiniteSkiesDungeons " + getPluginMeta().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("InfiniteSkiesDungeons disabled.");
    }
}

