package dev.sarmy.infiniteskies.dungeons.mob;

import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;
import java.util.Optional;

/** Creates and recognizes custom-mob spawn eggs. */
public final class CustomMobEggService {
    private final PersistentKeys keys;

    public CustomMobEggService(PersistentKeys keys) {
        this.keys = keys;
    }

    public ItemStack create(CustomMobDefinition definition) {
        ItemStack egg = ItemStack.of(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta meta = egg.getItemMeta();
        meta.displayName(Component.text("Spawn " + definition.displayName()));
        meta.getPersistentDataContainer().set(keys.spawnEggMobId(), PersistentDataType.STRING, definition.id());
        egg.setItemMeta(meta);
        return egg;
    }

    public Optional<String> mobId(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return Optional.empty();
        }
        return Optional.ofNullable(item.getItemMeta().getPersistentDataContainer()
                .get(keys.spawnEggMobId(), PersistentDataType.STRING));
    }
}
