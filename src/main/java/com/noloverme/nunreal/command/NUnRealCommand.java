package com.noloverme.nunreal.command;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.craft.CommandBlockRecipe;
import com.noloverme.nunreal.spawner.SpawnerItemFactory;
import com.noloverme.nunreal.spawner.SpawnerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class NUnRealCommand implements CommandExecutor, TabCompleter {
    private final NUnReal plugin;

    public NUnRealCommand(NUnReal plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/nunreal <reload|give|givespawner|blockcraft> [args...]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("nunreal.admin")) {
                sender.sendMessage("§cНет прав!");
                return true;
            }

            reloadPlugin(sender);
            return true;
        }

        if (subCommand.equals("give")) {
            if (!sender.hasPermission("nunreal.admin")) {
                sender.sendMessage("§cНет прав!");
                return true;
            }

            Player target = null;
            if (args.length >= 2) {
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cИгрок не найден!");
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage("§cУкажите игрока!");
                return true;
            }

            target.getInventory().addItem(CommandBlockRecipe.createCommandBlock(plugin));
            sender.sendMessage("§aБлок способностей выдан " + target.getName());
            return true;
        }

        if (subCommand.equals("givespawner")) {
            if (!sender.hasPermission("nunreal.admin")) {
                sender.sendMessage("§cНет прав!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§c/nunreal givespawner <mob> [player]");
                sender.sendMessage("§7Доступные мобы: SKELETON, ZOMBIE, WITHER_SKELETON, GHAST, CREEPER, HOGLIN, SHULKER, SPIDER, BLAZE, VINDICATOR, MAGMA_CUBE, SLIME, ENDERMAN, COW, MOOSHROOM, BREEZE, SNIFFER, IRON_GOLEM, ARMADILLO, WOLF, TURTLE, VILLAGER, SALMON, COD, CAT, DROWNED, SNOW_GOLEM");
                return true;
            }

            SpawnerType type = null;
            try {
                type = SpawnerType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cНеизвестный тип моба: " + args[1]);
                return true;
            }

            Player target = null;
            if (args.length >= 3) {
                target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage("§cИгрок не найден!");
                    return true;
                }
            } else if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage("§cУкажите игрока!");
                return true;
            }

            var spawnerItem = SpawnerItemFactory.createSpawnerItem(type, plugin.getConfigManager());
            if (spawnerItem == null) {
                sender.sendMessage("§cОшибка при создании спавнера!");
                return true;
            }

            target.getInventory().addItem(spawnerItem);
            String mobName = type.getDisplayName(plugin.getConfigManager());
            sender.sendMessage("§aСпавнер " + mobName + " выдан " + target.getName());
            return true;
        }

        if (subCommand.equals("blockcraft")) {
            if (!sender.hasPermission("nunreal.admin")) {
                sender.sendMessage("§cНет прав!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§c/nunreal blockcraft <material|id>");
                return true;
            }

            String itemName = args[1].toUpperCase();
            var blacklist = plugin.getConfigManager().getStringList("abilities.THRIFTY_CRAFT.blacklisted-items");

            if (blacklist.contains(itemName)) {
                blacklist.remove(itemName);
                plugin.getConfigManager().getConfig().set("abilities.THRIFTY_CRAFT.blacklisted-items", blacklist);
                plugin.getConfigManager().saveConfig();
                sender.sendMessage("§aПредмет " + itemName + " удалён из чёрного списка.");
            } else {
                blacklist.add(itemName);
                plugin.getConfigManager().getConfig().set("abilities.THRIFTY_CRAFT.blacklisted-items", blacklist);
                plugin.getConfigManager().saveConfig();
                sender.sendMessage("§aПредмет " + itemName + " добавлен в чёрный список.");
            }
            return true;
        }

        sender.sendMessage("§c/nunreal <reload|give|givespawner|blockcraft> [args...]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("nunreal.admin")) {
                if ("reload".startsWith(args[0].toLowerCase())) {
                    completions.add("reload");
                }
                if ("give".startsWith(args[0].toLowerCase())) {
                    completions.add("give");
                }
                if ("givespawner".startsWith(args[0].toLowerCase())) {
                    completions.add("givespawner");
                }
                if ("blockcraft".startsWith(args[0].toLowerCase())) {
                    completions.add("blockcraft");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give") && sender.hasPermission("nunreal.admin")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("givespawner") && sender.hasPermission("nunreal.admin")) {
                for (SpawnerType type : SpawnerType.values()) {
                    if (type.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(type.name());
                    }
                }
            } else if (args[0].equalsIgnoreCase("blockcraft") && sender.hasPermission("nunreal.admin")) {
                for (Material material : Material.values()) {
                    if (material.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(material.name());
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("givespawner") && sender.hasPermission("nunreal.admin")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }

    /**
     * Completely reload plugin configuration and related systems.
     */
    private void reloadPlugin(CommandSender sender) {
        try {
            plugin.getConfigManager().loadConfig();
            CommandBlockRecipe.registerRecipe(plugin);
            plugin.getSpawnerRecipeManager().reloadRecipes(plugin);

            String message = plugin.getConfigManager().getString("messages.config-reloaded", "§aКонфигурация перезагружена.");
            sender.sendMessage(message);

            sender.sendMessage("§a✓ Конфиг перезагружен");
            sender.sendMessage("§a✓ Рецепты способностей обновлены");
            sender.sendMessage("§a✓ Рецепты спавнеров обновлены");
            sender.sendMessage("§a✓ Меню переинициализировано");
        } catch (Exception e) {
            sender.sendMessage("§cОшибка при перезагрузке: " + e.getMessage());
            plugin.getLogger().severe("Ошибка при перезагрузке конфига: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
