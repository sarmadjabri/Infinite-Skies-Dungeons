package dev.sarmy.infiniteskies.dungeons.command;

import dev.sarmy.infiniteskies.dungeons.mob.CustomMobEggService;
import dev.sarmy.infiniteskies.dungeons.mob.CustomMobManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** Administration commands for direct custom-mob spawning and egg creation. */
public final class IsDungeonCommand implements CommandExecutor, TabCompleter {
    private final CustomMobManager mobs;
    private final CustomMobEggService eggs;

    public IsDungeonCommand(CustomMobManager mobs, CustomMobEggService eggs) {
        this.mobs = mobs;
        this.eggs = eggs;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("infiniteskiesdungeons.command.mob")) {
            sender.sendMessage(Component.text("You do not have permission to use this command."));
            return true;
        }
        if (args.length < 2 || !args[0].equalsIgnoreCase("mob")) {
            sender.sendMessage(Component.text("Usage: /isdungeon mob <list|spawn|egg> [mob-id]"));
            return true;
        }
        if (args[1].equalsIgnoreCase("list")) {
            sender.sendMessage(Component.text("Custom mobs: " + String.join(", ", mobs.definitions().stream().map(d -> d.id()).sorted().toList())));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can spawn mobs or receive spawn eggs."));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text("Specify a mob ID."));
            return true;
        }
        var definition = mobs.definition(args[2]);
        if (definition.isEmpty()) {
            sender.sendMessage(Component.text("Unknown custom mob: " + args[2]));
            return true;
        }
        if (args[1].equalsIgnoreCase("spawn")) {
            mobs.spawn(definition.get().id(), player.getLocation());
            sender.sendMessage(Component.text("Spawned " + definition.get().displayName() + "."));
            return true;
        }
        if (args[1].equalsIgnoreCase("egg")) {
            player.getInventory().addItem(eggs.create(definition.get()));
            sender.sendMessage(Component.text("Received a " + definition.get().displayName() + " spawn egg."));
            return true;
        }
        sender.sendMessage(Component.text("Usage: /isdungeon mob <list|spawn|egg> [mob-id]"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("mob");
        if (args.length == 2 && args[0].equalsIgnoreCase("mob")) return List.of("list", "spawn", "egg");
        if (args.length == 3 && args[0].equalsIgnoreCase("mob")) {
            List<String> ids = new ArrayList<>();
            for (var definition : mobs.definitions()) ids.add(definition.id());
            return ids;
        }
        return List.of();
    }
}
