package dev.sarmy.infiniteskies.dungeons.visual;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

/** Renders one consistent vanilla nameplate health bar above every custom mob. */
public final class MobHealthBarRenderer {
    private static final int SEGMENTS = 10;

    public void update(LivingEntity entity, CustomMobDefinition definition) {
        double maximum = entity.getAttribute(Attribute.MAX_HEALTH) == null
                ? definition.maximumHealth() : entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        double ratio = Math.clamp(entity.getHealth() / Math.max(1.0, maximum), 0.0, 1.0);
        int filled = (int) Math.ceil(ratio * SEGMENTS);
        TextColor healthColor = colorFor(ratio);
        Component bar = Component.text(" [", NamedTextColor.DARK_GRAY);
        for (int index = 0; index < SEGMENTS; index++) {
            bar = bar.append(Component.text("█", index < filled ? healthColor : NamedTextColor.DARK_GRAY));
        }
        entity.customName(Component.text(definition.displayName(), NamedTextColor.WHITE).append(bar).append(Component.text("]", NamedTextColor.DARK_GRAY)));
        entity.setCustomNameVisible(true);
    }

    private TextColor colorFor(double ratio) {
        if (ratio >= .5) return gradient(NamedTextColor.YELLOW, NamedTextColor.GREEN, (ratio - .5) * 2);
        return gradient(NamedTextColor.RED, NamedTextColor.YELLOW, ratio * 2);
    }

    private TextColor gradient(TextColor from, TextColor to, double progress) {
        int red = (int) Math.round(from.red() + (to.red() - from.red()) * progress);
        int green = (int) Math.round(from.green() + (to.green() - from.green()) * progress);
        int blue = (int) Math.round(from.blue() + (to.blue() - from.blue()) * progress);
        return TextColor.color(red, green, blue);
    }
}
