package com.noloverme.nunreal.spawner;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages registration and unregistration of spawner crafting recipes.
 */
public class SpawnerRecipeManager {
    private final JavaPlugin plugin;
    private final Map<SpawnerType, NamespacedKey> registeredRecipes = new HashMap<>();

    public SpawnerRecipeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register all enabled spawner recipes.
     */
    public void registerRecipes(NUnReal plugin) {
        ConfigManager config = plugin.getConfigManager();

        if (!config.getBoolean("spawners.enabled", true)) {
            plugin.getLogger().info("Spawner system is disabled in config");
            return;
        }

        for (SpawnerType type : SpawnerType.values()) {
            if (type.isEnabled(config)) {
                registerRecipe(type, config);
            }
        }

        plugin.getLogger().info("Registered " + registeredRecipes.size() + " spawner recipes");
    }

    /**
     * Register a single spawner recipe.
     */
    private void registerRecipe(SpawnerType type, ConfigManager config) {
        String recipePath = type.getRecipeKey();

        // Get shape
        List<String> shape = config.getStringList(recipePath + ".shape");
        if (shape == null || shape.isEmpty()) {
            plugin.getLogger().warning("No shape configured for spawner recipe: " + type.name());
            return;
        }

        if (shape.size() != 3) {
            plugin.getLogger().warning("Invalid shape size for spawner recipe: " + type.name() + " (expected 3 rows)");
            return;
        }

        // Create recipe
        NamespacedKey key = new NamespacedKey("nunreal", "spawner_" + type.name().toLowerCase());
        ShapedRecipe recipe = new ShapedRecipe(key, SpawnerItemFactory.createSpawnerItem(type, config));

        try {
            // Set shape rows - remove spaces for readability in config
            String row1 = shape.get(0).replace(" ", "");
            String row2 = shape.get(1).replace(" ", "");
            String row3 = shape.get(2).replace(" ", "");
            recipe.shape(row1, row2, row3);

            // Set ingredients
            String ingredientsPath = recipePath + ".ingredients";
            for (char c = 'A'; c <= 'Z'; c++) {
                String materialName = config.getString(ingredientsPath + "." + c, null);
                if (materialName != null) {
                    try {
                        Material material = Material.valueOf(materialName);
                        recipe.setIngredient(c, material);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid material for spawner recipe " + type.name() + ": " + materialName);
                        return;
                    }
                }
            }

            // Register recipe
            Bukkit.addRecipe(recipe);
            registeredRecipes.put(type, key);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register spawner recipe for " + type.name());
            e.printStackTrace();
        }
    }

    /**
     * Unregister all spawner recipes.
     */
    public void unregisterRecipes() {
        for (NamespacedKey key : registeredRecipes.values()) {
            removeRecipe(key);
        }
        registeredRecipes.clear();
        plugin.getLogger().info("Unregistered all spawner recipes");
    }

    /**
     * Reload all spawner recipes (unregister old ones and register new).
     */
    public void reloadRecipes(NUnReal plugin) {
        unregisterRecipes();
        registerRecipes(plugin);
        plugin.getLogger().info("Reloaded spawner recipes");
    }

    /**
     * Remove a recipe by its NamespacedKey.
     */
    private void removeRecipe(NamespacedKey key) {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe.getResult().hasItemMeta() &&
                recipe.getResult().getItemMeta() != null &&
                recipe.getResult().getType() == Material.SPAWNER &&
                SpawnerItemFactory.isCustomSpawner(recipe.getResult())) {
                iterator.remove();
            }
        }
    }
}
