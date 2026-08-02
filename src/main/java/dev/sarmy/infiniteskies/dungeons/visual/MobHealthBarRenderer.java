package dev.sarmy.infiniteskies.dungeons.visual;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Renders a separate health bar above an otherwise nameless custom mob. */
public final class MobHealthBarRenderer {
    private static final int SEGMENTS = 10;
    private final Map<UUID, TextDisplay> displays = new HashMap<>();
    public void update(LivingEntity entity, CustomMobDefinition definition) {
        double maximum = entity.getAttribute(Attribute.MAX_HEALTH) == null ? definition.maximumHealth() : entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        double ratio = Math.clamp(entity.getHealth() / Math.max(1.0, maximum), 0.0, 1.0);
        int filled = (int) Math.ceil(ratio * SEGMENTS);
        Component bar = Component.text("[", NamedTextColor.DARK_GRAY);
        TextColor color = colorFor(ratio);
        for (int index = 0; index < SEGMENTS; index++) bar = bar.append(Component.text("█", index < filled ? color : NamedTextColor.DARK_GRAY));
        entity.customName(null);
        entity.setCustomNameVisible(false);
        TextDisplay display = displays.get(entity.getUniqueId());
        if (display == null || !display.isValid()) { display = entity.getWorld().spawn(entity.getLocation(), TextDisplay.class); display.setPersistent(false); display.setBillboard(Display.Billboard.CENTER); displays.put(entity.getUniqueId(), display); }
        display.teleport(entity.getLocation().add(0, entity.getHeight() + .45, 0));
        display.text(bar.append(Component.text("]", NamedTextColor.DARK_GRAY)));
    }
    public void remove(UUID id) { TextDisplay display = displays.remove(id); if (display != null) display.remove(); }
    private TextColor colorFor(double ratio) { return ratio >= .5 ? gradient(NamedTextColor.YELLOW, NamedTextColor.GREEN, (ratio-.5)*2) : gradient(NamedTextColor.RED, NamedTextColor.YELLOW, ratio*2); }
    private TextColor gradient(TextColor from, TextColor to, double progress) { return TextColor.color((int)Math.round(from.red()+(to.red()-from.red())*progress),(int)Math.round(from.green()+(to.green()-from.green())*progress),(int)Math.round(from.blue()+(to.blue()-from.blue())*progress)); }
}
