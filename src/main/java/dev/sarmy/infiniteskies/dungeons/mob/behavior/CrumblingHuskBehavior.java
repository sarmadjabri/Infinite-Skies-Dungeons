package dev.sarmy.infiniteskies.dungeons.mob.behavior;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import dev.sarmy.infiniteskies.dungeons.mob.MobRank;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

/** Dedicated combat holder for the Crumbling Husk. */
public final class CrumblingHuskBehavior implements MobBehavior {
    public static final String ID = "crumbling_husk";
    public static final double HEALTH = 50.0;
    public static final double MELEE_DAMAGE = 5.0;
    public static final int ATTACK_COOLDOWN_TICKS = 60;
    public static final double MOVEMENT_SPEED = 0.2185;

    @Override public String mobId() { return ID; }

    @Override public CustomMobDefinition definition() {
        return new CustomMobDefinition(ID, "Crumbling Husk", EntityType.HUSK,
                MobRank.NORMAL, HEALTH, MELEE_DAMAGE, MOVEMENT_SPEED);
    }

    @Override
    public void applyEquipment(LivingEntity entity) {
        ItemStack sword = ItemStack.of(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        sword.setItemMeta(meta);
        entity.getEquipment().setItemInMainHand(sword);
        entity.getEquipment().setItemInMainHandDropChance(0.0F);
    }
}
