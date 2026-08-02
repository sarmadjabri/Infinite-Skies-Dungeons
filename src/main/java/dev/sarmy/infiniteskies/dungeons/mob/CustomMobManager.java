package dev.sarmy.infiniteskies.dungeons.mob;

import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import dev.sarmy.infiniteskies.dungeons.mob.behavior.MobBehavior;
import dev.sarmy.infiniteskies.dungeons.combat.CombatTargets;
import dev.sarmy.infiniteskies.dungeons.visual.MobHealthBarRenderer;
import org.bukkit.entity.Mob;

/** Spawns and identifies custom mobs through persistent entity data. */
public final class CustomMobManager {
    private static final int DATA_VERSION = 1;

    private final PersistentKeys keys;
    private final Map<String, CustomMobDefinition> definitions = new HashMap<>();
    private final Map<UUID, CustomMobInstance> instances = new HashMap<>();
    private final Map<String, MobBehavior> behaviors = new HashMap<>();
    private final MobHealthBarRenderer healthBars = new MobHealthBarRenderer();

    public CustomMobManager(PersistentKeys keys) {
        this.keys = keys;
    }

    public void register(CustomMobDefinition definition) {
        definitions.put(definition.id(), definition);
    }

    public void register(MobBehavior behavior) {
        behaviors.put(behavior.mobId(), behavior);
        register(behavior.definition());
    }

    public Optional<CustomMobDefinition> definition(String id) {
        return Optional.ofNullable(definitions.get(id));
    }

    public Collection<CustomMobDefinition> definitions() {
        return List.copyOf(definitions.values());
    }

    public Optional<CustomMobInstance> instance(UUID entityId) {
        return Optional.ofNullable(instances.get(entityId));
    }

    public CustomMobInstance spawn(String id, Location location) {
        CustomMobDefinition definition = definition(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown custom mob ID: " + id));
        LivingEntity entity = (LivingEntity) Objects.requireNonNull(location.getWorld(), "location world")
                .spawnEntity(location, definition.entityType());
        entity.customName(null);
        entity.setCustomNameVisible(false);
        applyDefinitionAttributes(entity, definition);
        entity.setHealth(definition.maximumHealth());
        MobBehavior behavior = behaviors.get(definition.id());
        if (behavior != null) {
            behavior.applyEquipment(entity);
        }
        entity.getPersistentDataContainer().set(keys.dataVersion(), PersistentDataType.INTEGER, DATA_VERSION);
        entity.getPersistentDataContainer().set(keys.mobId(), PersistentDataType.STRING, definition.id());
        UUID instanceId = UUID.randomUUID();
        entity.getPersistentDataContainer().set(keys.instanceId(), PersistentDataType.STRING, instanceId.toString());
        entity.getPersistentDataContainer().set(keys.mobRank(), PersistentDataType.STRING, definition.rank().name());
        CustomMobInstance instance = new CustomMobInstance(entity.getUniqueId(), instanceId, definition);
        instances.put(entity.getUniqueId(), instance);
        return instance;
    }

    public boolean isManaged(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(keys.mobId(), PersistentDataType.STRING);
    }

    /**
     * Reconnects a persisted custom mob to its live controller.  Custom-mob
     * identity is stored on the entity, whereas the runtime instance map is not;
     * this is therefore required after a restart or after its chunk is loaded.
     */
    public void restore(Entity entity) {
        if (!(entity instanceof LivingEntity living) || instances.containsKey(living.getUniqueId())) return;
        String mobId = living.getPersistentDataContainer().get(keys.mobId(), PersistentDataType.STRING);
        CustomMobDefinition definition = definitions.get(mobId);
        if (definition == null) return;

        String persistedInstanceId = living.getPersistentDataContainer().get(keys.instanceId(), PersistentDataType.STRING);
        UUID instanceId;
        try {
            instanceId = persistedInstanceId == null ? UUID.randomUUID() : UUID.fromString(persistedInstanceId);
        } catch (IllegalArgumentException ignored) {
            instanceId = UUID.randomUUID();
        }
        instances.put(living.getUniqueId(), new CustomMobInstance(living.getUniqueId(), instanceId, definition));
        // Reapply definition attributes so updating a behavior takes effect on
        // already-spawned mobs after the server is restarted.
        applyDefinitionAttributes(living, definition);
        MobBehavior behavior = behaviors.get(mobId);
        if (behavior != null) behavior.applyEquipment(living);
    }

    /** Restores all entities that are already loaded when this plugin enables. */
    public void restoreLoadedEntities() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(this::restore));
    }

    /** Returns the authoritative persistent custom-mob identifier, including after a server restart. */
    public Optional<String> mobId(LivingEntity entity) {
        return Optional.ofNullable(entity.getPersistentDataContainer().get(keys.mobId(), PersistentDataType.STRING));
    }

    /** Updates active, loaded custom entities; unloaded entities keep only PDC identity. */
    public void tick(long tick) {
        for (CustomMobInstance instance : List.copyOf(instances.values())) {
            var entity = Bukkit.getEntity(instance.entityId());
            if (!(entity instanceof LivingEntity living) || !living.isValid() || living.isDead()) {
                instances.remove(instance.entityId());
                healthBars.remove(instance.entityId());
                continue;
            }
            MobBehavior behavior = behaviors.get(instance.definition().id());
            if (tick % 5 == 0 && living instanceof Mob mob) {
                mob.setTarget(CombatTargets.nearestPlayer(living, 32.0));
            }
            healthBars.update(living, instance.definition());
            if (behavior != null) behavior.tick(living, tick);
        }
    }

    private void setAttribute(LivingEntity entity, Attribute attribute, double value) {
        if (entity.getAttribute(attribute) != null) {
            entity.getAttribute(attribute).setBaseValue(value);
        }
    }

    private void applyDefinitionAttributes(LivingEntity entity, CustomMobDefinition definition) {
        setAttribute(entity, Attribute.MAX_HEALTH, definition.maximumHealth());
        setAttribute(entity, Attribute.ATTACK_DAMAGE, definition.attackDamage());
        setAttribute(entity, Attribute.MOVEMENT_SPEED, definition.movementSpeed());
        setAttribute(entity, Attribute.KNOCKBACK_RESISTANCE, 1.0);
        if (entity.getHealth() > definition.maximumHealth()) {
            entity.setHealth(definition.maximumHealth());
        }
    }
}
