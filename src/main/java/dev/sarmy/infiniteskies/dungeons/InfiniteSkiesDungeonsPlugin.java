package dev.sarmy.infiniteskies.dungeons;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobManager;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobEggService;
import dev.sarmy.infiniteskies.dungeons.command.IsDungeonCommand;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.MossboundBruteBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.CrumblingHuskBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RiftArcherBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RuinColossusBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.SkyReaverBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.StormCallerBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.ObservatoryWatcherBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.VenomSpitterBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.GaleAcolyteBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.MossPriestBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.VoidMenderBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.GraveChanterBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RuinStandardBearerBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.StoneBinderBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.CeilingStalkerBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.MimicHuskBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.DustPhantomBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.BuriedGuardianBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.FalseStatueBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.EmberWispBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.StormWispBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.FrostWispBehavior;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.RiftMineBehavior;
import dev.sarmy.infiniteskies.dungeons.listener.CustomMobSpawnListener;
import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import dev.sarmy.infiniteskies.dungeons.scheduler.CentralTickService;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap for the Infinite Skies dungeon encounter framework.
 */
public final class InfiniteSkiesDungeonsPlugin extends JavaPlugin {
    private CustomMobManager mobManager;
    private PersistentKeys persistentKeys;
    private CustomMobEggService eggService;
    private CentralTickService tickService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        persistentKeys = new PersistentKeys(this);
        mobManager = new CustomMobManager(persistentKeys);
        mobManager.register(new CrumblingHuskBehavior());
        mobManager.register(new MossboundBruteBehavior().definition());
        mobManager.register(new RiftArcherBehavior().definition());
        mobManager.register(new StormCallerBehavior().definition());
        mobManager.register(new SkyReaverBehavior().definition());
        mobManager.register(new RuinColossusBehavior().definition());
        mobManager.register(new ObservatoryWatcherBehavior());
        mobManager.register(new VenomSpitterBehavior());
        mobManager.register(new GaleAcolyteBehavior());
        mobManager.register(new MossPriestBehavior());
        mobManager.register(new VoidMenderBehavior());
        mobManager.register(new GraveChanterBehavior());
        mobManager.register(new RuinStandardBearerBehavior());
        mobManager.register(new StoneBinderBehavior());
        mobManager.register(new CeilingStalkerBehavior());
        mobManager.register(new MimicHuskBehavior());
        mobManager.register(new DustPhantomBehavior());
        mobManager.register(new BuriedGuardianBehavior());
        mobManager.register(new FalseStatueBehavior());
        mobManager.register(new EmberWispBehavior());
        mobManager.register(new StormWispBehavior());
        mobManager.register(new FrostWispBehavior());
        mobManager.register(new RiftMineBehavior());
        eggService = new CustomMobEggService(persistentKeys);
        tickService = new CentralTickService(this);
        tickService.register(mobManager::tick);
        tickService.start();
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
        if (tickService != null) tickService.stop();
        getLogger().info("InfiniteSkiesDungeons disabled.");
    }
}
