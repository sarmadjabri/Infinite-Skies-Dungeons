package dev.sarmy.infiniteskies.dungeons.mob;

import dev.sarmy.infiniteskies.dungeons.persistence.PersistentKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.entity.Mob;

/** Spawns and identifies custom mobs through persistent entity data. */
public final class CustomMobManager {
    private static final int DATA_VERSION = 1;

    private final PersistentKeys keys;
    private final Map<String, CustomMobDefinition> definitions = new HashMap<>();
    private final Map<UUID, CustomMobInstance> instances = new HashMap<>();
    private final Map<String, MobBehavior> behaviors = new HashMap<>();

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
        entity.customName(Component.text(definition.displayName()));
        entity.setCustomNameVisible(true);
        setAttribute(entity, Attribute.MAX_HEALTH, definition.maximumHealth());
        entity.setHealth(definition.maximumHealth());
        setAttribute(entity, Attribute.ATTACK_DAMAGE, definition.attackDamage());
        setAttribute(entity, Attribute.MOVEMENT_SPEED, definition.movementSpeed());
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

    /** Updates active, loaded custom entities; unloaded entities keep only PDC identity. */
    public void tick(long tick) {
        for (CustomMobInstance instance : List.copyOf(instances.values())) {
            var entity = Bukkit.getEntity(instance.entityId());
            if (!(entity instanceof LivingEntity living) || !living.isValid() || living.isDead()) {
                instances.remove(instance.entityId());
                continue;
            }
            MobBehavior behavior = behaviors.get(instance.definition().id());
            if (tick % 5 == 0 && living instanceof Mob mob) {
                mob.setTarget(CombatTargets.nearestPlayer(living, 32.0));
            }
            if (behavior != null) behavior.tick(living, tick);
        }
    }

    private void setAttribute(LivingEntity entity, Attribute attribute, double value) {
        if (entity.getAttribute(attribute) != null) {
            entity.getAttribute(attribute).setBaseValue(value);
        }
    }
}
