package com.noloverme.nunreal.craft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CommandBlockRecipe {
    public static void registerRecipe(JavaPlugin plugin) {
        boolean enabled = plugin.getConfig().getBoolean("craft.enabled", true);
        if (!enabled) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "command_block_recipe");

        ShapedRecipe recipe = new ShapedRecipe(key, createCommandBlock(plugin));

        String shape1 = plugin.getConfig().getString("craft.shape.1", "ABA");
        String shape2 = plugin.getConfig().getString("craft.shape.2", "CAC");
        String shape3 = plugin.getConfig().getString("craft.shape.3", "ADA");

        recipe.shape(shape1, shape2, shape3);

        String slotA = plugin.getConfig().getString("craft.slots.A", "NETHERITE_INGOT");
        String slotB = plugin.getConfig().getString("craft.slots.B", "AMETHYST_SHARD");
        String slotC = plugin.getConfig().getString("craft.slots.C", "BLAZE_ROD");
        String slotD = plugin.getConfig().getString("craft.slots.D", "DIAMOND");

        recipe.setIngredient('A', Material.valueOf(slotA));
        recipe.setIngredient('B', Material.valueOf(slotB));
        recipe.setIngredient('C', Material.valueOf(slotC));
        recipe.setIngredient('D', Material.valueOf(slotD));

        Bukkit.addRecipe(recipe);
        plugin.getLogger().info("Ability block recipe registered");
    }

    public static ItemStack createCommandBlock(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = plugin.getConfig().getString("craft.result-name", "§6Блок Способностей");
            meta.displayName(net.kyori.adventure.text.Component.text(name));

            List<String> lore = plugin.getConfig().getStringList("craft.result-lore");
            if (!lore.isEmpty()) {
                meta.lore(lore.stream()
                    .map(s -> net.kyori.adventure.text.Component.text(s))
                    .toList());
            }

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(new NamespacedKey(plugin, "ability_block"), PersistentDataType.BYTE, (byte) 1);

            item.setItemMeta(meta);
        }

        return item;
    }

    public static boolean isAbilityBlock(ItemStack item) {
        if (item == null || item.getType() != Material.COMMAND_BLOCK) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("nunreal", "ability_block");

        return pdc.has(key, PersistentDataType.BYTE);
    }
}
