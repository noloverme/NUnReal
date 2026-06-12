package com.noloverme.nunreal.spawner;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.config.ConfigManager;
import com.noloverme.nunreal.util.MaterialUtils;
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
    private final Map<String, NamespacedKey> registeredRecipes = new HashMap<>();

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

        SpawnerTypeRegistry.initialize(config);

        for (SpawnerTypeRegistry.SpawnerTypeData typeData : SpawnerTypeRegistry.getAll()) {
            if (isEnabled(typeData.getEnumName(), config)) {
                registerRecipe(typeData, config);
            }
        }

        plugin.getLogger().info("Registered " + registeredRecipes.size() + " spawner recipes");
    }

    /**
     * Register a single spawner recipe.
     */
    private void registerRecipe(SpawnerTypeRegistry.SpawnerTypeData typeData, ConfigManager config) {
        String recipePath = "spawners.recipes." + typeData.getEnumName();

        // Get shape
        List<String> shape = config.getStringList(recipePath + ".shape");
        if (shape == null || shape.isEmpty()) {
            plugin.getLogger().warning("No shape configured for spawner recipe: " + typeData.getEnumName());
            return;
        }

        if (shape.size() != 3) {
            plugin.getLogger().warning("Invalid shape size for spawner recipe: " + typeData.getEnumName() + " (expected 3 rows)");
            return;
        }

        // Create recipe
        NamespacedKey key = new NamespacedKey("nunreal", "spawner_" + typeData.getEnumName().toLowerCase());
        ShapedRecipe recipe = new ShapedRecipe(key, SpawnerItemFactory.createSpawnerItem(typeData, config));

        try {
            // Set shape rows - remove spaces for readability in config
            String row1 = shape.get(0).replace(" ", "");
            String row2 = shape.get(1).replace(" ", "");
            String row3 = shape.get(2).replace(" ", "");
            recipe.shape(row1, row2, row3);

            // Set ingredients - support unlimited characters beyond A-Z
            String ingredientsPath = recipePath + ".ingredients";
            org.bukkit.configuration.ConfigurationSection ingredientsSection = config.getConfigurationSection(ingredientsPath);
            if (ingredientsSection != null) {
                for (String key_str : ingredientsSection.getKeys(false)) {
                    String materialName = config.getString(ingredientsPath + "." + key_str, null);
                    if (materialName != null && !key_str.isEmpty()) {
                        Material material = MaterialUtils.getMaterial(materialName);
                        if (material != null) {
                            recipe.setIngredient(key_str.charAt(0), material);
                        } else {
                            plugin.getLogger().warning("Invalid material for spawner recipe " + typeData.getEnumName() + ": " + materialName);
                            return;
                        }
                    }
                }
            }

            // Register recipe
            Bukkit.addRecipe(recipe);
            registeredRecipes.put(typeData.getEnumName(), key);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register spawner recipe for " + typeData.getEnumName());
            e.printStackTrace();
        }
    }

    private boolean isEnabled(String mobName, ConfigManager config) {
        String path = "spawners.recipes." + mobName + ".enabled";
        return config.getBoolean(path, true);
    }

    public void unregisterRecipes() {
        for (NamespacedKey key : registeredRecipes.values()) {
            removeRecipe(key);
        }
        registeredRecipes.clear();
        plugin.getLogger().info("Unregistered all spawner recipes");
    }

    public void reloadRecipes(NUnReal plugin) {
        unregisterRecipes();
        registerRecipes(plugin);
        plugin.getLogger().info("Reloaded spawner recipes");
    }

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
