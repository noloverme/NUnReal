package com.noloverme.nunreal.craft;

import com.noloverme.nunreal.util.MaterialUtils;
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

        // Read shape configuration
        Object shapeConfig = plugin.getConfig().get("craft.shape");
        String[] shapeArray = new String[3];

        if (shapeConfig instanceof List) {
            List<?> shapeList = (List<?>) shapeConfig;
            for (int i = 0; i < Math.min(3, shapeList.size()); i++) {
                Object item = shapeList.get(i);
                shapeArray[i] = item != null ? item.toString().replace(" ", "") : "";
            }
        } else {
            // Fallback for old format
            shapeArray[0] = plugin.getConfig().getString("craft.shape.0", "CDE");
            shapeArray[1] = plugin.getConfig().getString("craft.shape.1", "FLF");
            shapeArray[2] = plugin.getConfig().getString("craft.shape.2", "MNO");
        }

        recipe.shape(shapeArray[0], shapeArray[1], shapeArray[2]);

        try {
            // Read slots configuration
            for (char c = 'A'; c <= 'Z'; c++) {
                String materialName = plugin.getConfig().getString("craft.slots." + c, null);
                if (materialName != null) {
                    Material material = MaterialUtils.getMaterialOrThrow(materialName);
                    recipe.setIngredient(c, material);
                }
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Failed to register ability block recipe: " + e.getMessage());
            return;
        }

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
