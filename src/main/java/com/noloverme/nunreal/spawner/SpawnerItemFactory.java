package com.noloverme.nunreal.spawner;

import com.noloverme.nunreal.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating custom spawner items with PDC markers and configuration-driven properties.
 */
public class SpawnerItemFactory {
    private static final String PDC_NAMESPACE = "nunreal";
    private static final String PDC_KEY = "spawner_mob";

    private SpawnerItemFactory() {
        // Static utility class
    }

    /**
     * Create a custom spawner item for the given type.
     */
    public static ItemStack createSpawnerItem(SpawnerType type, ConfigManager config) {
        if (type == null || config == null) {
            return null;
        }

        ItemStack item = new ItemStack(Material.SPAWNER, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        // Set display name
        String nameFormat = config.getString("spawners.item.name-format", "§6Спавнер: §e{mob}");
        String displayName = nameFormat.replace("{mob}", type.getDisplayName(config));
        meta.displayName(Component.text(displayName));

        // Set lore
        List<String> lorePaths = config.getStringList("spawners.item.lore");
        List<Component> loreComponents = new ArrayList<>();
        for (String loreLine : lorePaths) {
            loreComponents.add(Component.text(loreLine));
        }
        meta.lore(loreComponents);

        // Add enchant glow if configured
        if (config.getBoolean("spawners.item.enchant-glow", true)) {
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        // Hide attributes
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);

        // Add PDC marker
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(PDC_NAMESPACE, PDC_KEY);
        pdc.set(key, PersistentDataType.STRING, type.name());

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Check if an item is a custom spawner created by the plugin.
     */
    public static boolean isCustomSpawner(ItemStack item) {
        if (item == null || item.getType() != Material.SPAWNER) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(PDC_NAMESPACE, PDC_KEY);
        return pdc.has(key, PersistentDataType.STRING);
    }

    /**
     * Get the spawner type from a custom spawner item.
     * Returns null if the item is not a valid custom spawner.
     */
    public static SpawnerType getSpawnerType(ItemStack item) {
        if (!isCustomSpawner(item)) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(PDC_NAMESPACE, PDC_KEY);
        String typeName = pdc.get(key, PersistentDataType.STRING);

        if (typeName == null) {
            return null;
        }

        try {
            return SpawnerType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
