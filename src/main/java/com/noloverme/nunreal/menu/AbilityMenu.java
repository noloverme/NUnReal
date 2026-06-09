package com.noloverme.nunreal.menu;

import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AbilityMenu {
    private final JavaPlugin plugin;
    private final Inventory inventory;
    private final CommandBlockData blockData;

    public AbilityMenu(JavaPlugin plugin, CommandBlockData blockData) {
        this.plugin = plugin;
        this.blockData = blockData;

        String title = plugin.getConfig().getString("menu.title", "§8Выбор способности");
        int size = plugin.getConfig().getInt("menu.size", 9);

        this.inventory = Bukkit.createInventory(null, size, title);
        fillInventory();
    }

    private void fillInventory() {
        boolean fillerEnabled = plugin.getConfig().getBoolean("menu.filler.enabled", true);
        String fillerMaterial = plugin.getConfig().getString("menu.filler.material", "GRAY_STAINED_GLASS_PANE");
        String fillerName = plugin.getConfig().getString("menu.filler.name", " ");

        if (fillerEnabled) {
            try {
                Material material = Material.valueOf(fillerMaterial);
                ItemStack filler = new ItemStack(material, 1);
                ItemMeta meta = filler.getItemMeta();

                if (meta != null) {
                    meta.displayName(net.kyori.adventure.text.Component.text(fillerName));
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    filler.setItemMeta(meta);
                }

                for (int i = 0; i < inventory.getSize(); i++) {
                    if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                        inventory.setItem(i, filler);
                    }
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid filler material: " + fillerMaterial);
            }
        }

        ConfigurationSection abilitiesSection = plugin.getConfig().getConfigurationSection("menu.abilities");
        if (abilitiesSection == null) {
            return;
        }

        for (String abilityName : abilitiesSection.getKeys(false)) {
            try {
                AbilityType ability = AbilityType.valueOf(abilityName);
                ItemStack item = createAbilityItem(ability);
                if (item != null) {
                    String slotPath = "menu.abilities." + abilityName + ".slot";
                    int slot = plugin.getConfig().getInt(slotPath, -1);
                    if (slot >= 0) {
                        inventory.setItem(slot, item);
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        ConfigurationSection disableSection = plugin.getConfig().getConfigurationSection("menu.disable-button");
        if (disableSection != null) {
            int slot = disableSection.getInt("slot", 8);
            ItemStack item = createDisableButton();
            if (item != null) {
                inventory.setItem(slot, item);
            }
        }
    }

    private ItemStack createAbilityItem(AbilityType ability) {
        String path = "menu.abilities." + ability.name();
        String materialName = plugin.getConfig().getString(path + ".material", "STONE");
        String name = plugin.getConfig().getString(path + ".name", ability.getDisplayName());
        List<String> lore = plugin.getConfig().getStringList(path + ".lore");

        try {
            Material material = Material.valueOf(materialName);
            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.displayName(net.kyori.adventure.text.Component.text(name));

                List<net.kyori.adventure.text.Component> loreComponents = new ArrayList<>();
                for (String loreLine : lore) {
                    loreComponents.add(net.kyori.adventure.text.Component.text(loreLine));
                }

                if (blockData.getAbility() == ability) {
                    String activeLine = plugin.getConfig().getString(path + ".active-lore-line", "§aАктивно");
                    loreComponents.add(net.kyori.adventure.text.Component.text(activeLine));
                    meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                meta.lore(loreComponents);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }

            return item;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material: " + materialName);
            return null;
        }
    }

    private ItemStack createDisableButton() {
        String materialName = plugin.getConfig().getString("menu.disable-button.material", "BARRIER");
        String name = plugin.getConfig().getString("menu.disable-button.name", "§cОтключить");
        List<String> lore = plugin.getConfig().getStringList("menu.disable-button.lore");

        try {
            Material material = Material.valueOf(materialName);
            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.displayName(net.kyori.adventure.text.Component.text(name));
                meta.lore(lore.stream()
                    .map(s -> net.kyori.adventure.text.Component.text(s))
                    .toList());
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }

            return item;
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material: " + materialName);
            return null;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public CommandBlockData getBlockData() {
        return blockData;
    }
}
