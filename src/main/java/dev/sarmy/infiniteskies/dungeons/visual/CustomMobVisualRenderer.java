package dev.sarmy.infiniteskies.dungeons.visual;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Renders a custom-model item above an invisible vanilla controller.
 *
 * The controller remains authoritative for AI, damage, hitboxes and persistent
 * dungeon state. The ItemDisplay is visual-only, so two mobs that share a
 * vanilla entity type can still use different resource-pack models.
 */
public final class CustomMobVisualRenderer {
    private static final String ASSET_NAMESPACE = "infiniteskies";
    private final PersistentKeys keys;
    private final Map<UUID, UUID> visualIds = new HashMap<>();

    public CustomMobVisualRenderer(PersistentKeys keys) {
        this.keys = keys;
    }

    public void update(LivingEntity controller, CustomMobDefinition definition) {
        // Only plugin-managed controllers are hidden. Normal Minecraft mobs
        // retain their vanilla look and never receive an overlay.
        controller.setInvisible(true);
        ItemDisplay visual = findOrCreate(controller, definition);
        if (visual == null) return;
        visual.setInvisible(controller.getScoreboardTags().contains("isdungeon_hide_visual"));

        VisualProfile profile = VisualProfile.forMob(definition.id());
        Location location = controller.getLocation().clone().add(0.0, profile.yOffset(), 0.0);
        location.setPitch(0.0F);
        visual.teleport(location);
        visual.setRotation(controller.getYaw(), 0.0F);
    }

    public void remove(UUID controllerId) {
        UUID visualId = visualIds.remove(controllerId);
        if (visualId == null) return;
        Entity entity = org.bukkit.Bukkit.getEntity(visualId);
        if (entity != null) entity.remove();
    }

    /** Height used to keep the shared health bar above the visual, not its hidden controller. */
    public double visualHeight(String mobId) {
        VisualProfile profile = VisualProfile.forMob(mobId);
        return profile.modelHeight() * profile.scale() + profile.yOffset();
    }

    private ItemDisplay findOrCreate(LivingEntity controller, CustomMobDefinition definition) {
        UUID knownId = visualIds.get(controller.getUniqueId());
        Entity known = knownId == null ? null : org.bukkit.Bukkit.getEntity(knownId);
        if (known instanceof ItemDisplay display && display.isValid()) return display;

        // On a restart the runtime map is empty but the display entity may have
        // been persisted with the chunk. Reconnect it instead of duplicating it.
        for (ItemDisplay display : controller.getWorld().getEntitiesByClass(ItemDisplay.class)) {
            String owner = display.getPersistentDataContainer().get(keys.visualController(), PersistentDataType.STRING);
            if (controller.getUniqueId().toString().equals(owner)) {
                visualIds.put(controller.getUniqueId(), display.getUniqueId());
                return display;
            }
        }

        Location location = controller.getLocation();
        ItemDisplay display = controller.getWorld().spawn(location, ItemDisplay.class);
        display.getPersistentDataContainer().set(keys.visualController(), PersistentDataType.STRING,
                controller.getUniqueId().toString());
        display.setPersistent(true);
        display.setBillboard(Display.Billboard.FIXED);
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        display.setTeleportDuration(1);
        display.setViewRange(64.0F);
        display.setShadowRadius(.45F);
        display.setShadowStrength(.75F);
        display.setTransformation(new Transformation(new Vector3f(), new Quaternionf(),
                new Vector3f(VisualProfile.forMob(definition.id()).scale()), new Quaternionf()));
        display.setItemStack(modelItem(definition.id()));
        visualIds.put(controller.getUniqueId(), display.getUniqueId());
        return display;
    }

    private ItemStack modelItem(String mobId) {
        ItemStack item = ItemStack.of(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey(ASSET_NAMESPACE, "mob/" + mobId));
        item.setItemMeta(meta);
        return item;
    }

    private record VisualProfile(float scale, double yOffset, double modelHeight) {
        static VisualProfile forMob(String id) {
            return switch (id) {
                case "mossbound_brute" -> new VisualProfile(1.35F, 0.02D, 2.0D);
                case "ruin_colossus" -> new VisualProfile(1.40F, 0.02D, 2.0D);
                case "ruin_crawler", "venom_spitter", "ceiling_stalker" -> new VisualProfile(.90F, .02D, 12D / 16D);
                case "sky_reaver", "dust_phantom" -> new VisualProfile(.95F, .55D, 24D / 16D);
                case "storm_wisp", "frost_wisp", "gale_acolyte" -> new VisualProfile(.95F, .55D, 18D / 16D);
                case "ember_wisp", "rift_mine" -> new VisualProfile(.72F, .06D, 18D / 16D);
                default -> new VisualProfile(1.0F, .02D, 2.0D);
            };
        }
    }
}
