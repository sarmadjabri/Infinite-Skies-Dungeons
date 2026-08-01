package dev.sarmy.infiniteskies.dungeons.listener;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobEggService;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobManager;
import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

/** Routes tagged spawn eggs and tagged vanilla spawners through the mob manager. */
public final class CustomMobSpawnListener implements Listener {
    private final CustomMobManager mobManager;
    private final CustomMobEggService eggs;
    private final PersistentKeys keys;

    public CustomMobSpawnListener(CustomMobManager mobManager, CustomMobEggService eggs, PersistentKeys keys) {
        this.mobManager = mobManager;
        this.eggs = eggs;
        this.keys = keys;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEggUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        eggs.mobId(event.getItem()).ifPresent(mobId -> {
            event.setCancelled(true);
            if (event.getClickedBlock().getState() instanceof CreatureSpawner spawner
                    && spawner instanceof TileState tileState) {
                tileState.getPersistentDataContainer().set(keys.spawnerMobId(), PersistentDataType.STRING, mobId);
                tileState.update();
                event.getPlayer().sendMessage("Configured this spawner for " + mobId + ".");
                return;
            }
            mobManager.spawn(mobId, event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(.5, 0, .5));
            if (!event.getPlayer().getGameMode().isInvulnerable()) {
                event.getItem().subtract(1);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        if (!(event.getSpawner() instanceof TileState tileState)) {
            return;
        }
        String mobId = tileState.getPersistentDataContainer().get(keys.spawnerMobId(), PersistentDataType.STRING);
        if (mobId == null || mobManager.definition(mobId).isEmpty()) {
            return;
        }
        event.setCancelled(true);
        mobManager.spawn(mobId, event.getLocation());
    }
}
