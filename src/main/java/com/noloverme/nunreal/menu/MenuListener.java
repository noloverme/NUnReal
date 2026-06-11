package com.noloverme.nunreal.menu;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import com.noloverme.nunreal.util.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MenuListener implements Listener {
    private final NUnReal plugin;
    private final Map<String, CommandBlockData> openMenus = new HashMap<>();

    public MenuListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        String menuTitle = plugin.getConfigManager().getString("menu.title", "§8Выбор способности");

        if (!title.equals(menuTitle)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() == event.getView().getBottomInventory()) {
            return;
        }

        if (event.getClick() == ClickType.SWAP_OFFHAND || event.getClick() == ClickType.NUMBER_KEY) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        CommandBlockData blockData = openMenus.get(player.getUniqueId().toString());

        if (blockData == null) {
            player.closeInventory();
            return;
        }

        int slot = event.getSlot();
        AbilityType selected = getAbilityFromSlot(slot);

        if (selected == null) {
            return;
        }

        if (selected == AbilityType.NONE) {
            plugin.getBlockManager().updateAbility(blockData.getKey(), AbilityType.NONE);
            String message = plugin.getConfigManager().getString("economy.messages.ability-disabled", "§eСпособность отключена.");
            player.sendMessage(message);
            player.closeInventory();
            openMenus.remove(player.getUniqueId().toString());
            syncAbilityForChunk(player);
            return;
        }

        if (blockData.getAbility() == selected) {
            String message = plugin.getConfigManager().getString("economy.messages.already-active", "§eЭта способность уже активна в чанке.");
            player.sendMessage(message);
            return;
        }

        String costPath = "economy.ability-costs." + selected.name();
        String costMaterial;
        int costAmount;

        if (plugin.getConfigManager().getConfig().contains(costPath)) {
            costMaterial = plugin.getConfigManager().getString(costPath + ".material", "DIAMOND");
            costAmount = plugin.getConfigManager().getInt(costPath + ".amount", 1);
        } else {
            costMaterial = plugin.getConfigManager().getString("economy.default-cost.material", "DIAMOND");
            costAmount = plugin.getConfigManager().getInt("economy.default-cost.amount", 1);
        }

        Material material = MaterialUtils.getMaterial(costMaterial);
        if (material == null) {
            player.sendMessage("§cОшибка конфигурации материала стоимости!");
            return;
        }

        if (player.getInventory().containsAtLeast(new ItemStack(material), costAmount)) {
            player.getInventory().removeItem(new ItemStack(material, costAmount));
            plugin.getBlockManager().updateAbility(blockData.getKey(), selected);

            String message = plugin.getConfigManager().getString("economy.messages.ability-set", "§aСпособность §e{ability} §aактивирована!");
            message = message.replace("{ability}", selected.getDisplayName());
            player.sendMessage(message);

            player.closeInventory();
            openMenus.remove(player.getUniqueId().toString());

            syncAbilityForChunk(player);
        } else {
            String message = plugin.getConfigManager().getString("economy.messages.not-enough", "§cНедостаточно {item}!");
            message = message.replace("{item}", costMaterial).replace("{amount}", String.valueOf(costAmount));
            player.sendMessage(message);
        }
    }

    private AbilityType getAbilityFromSlot(int slot) {
        for (AbilityType ability : AbilityType.values()) {
            if (ability == AbilityType.NONE) {
                continue;
            }

            String path = "menu.abilities." + ability.name() + ".slot";
            int abilitySlot = plugin.getConfigManager().getInt(path, -1);

            if (abilitySlot == slot) {
                return ability;
            }
        }

        int disableSlot = plugin.getConfigManager().getInt("menu.disable-button.slot", 8);
        if (disableSlot == slot) {
            return AbilityType.NONE;
        }

        return null;
    }

    public void openMenu(Player player, CommandBlockData blockData) {
        AbilityMenu menu = new AbilityMenu(plugin, blockData);
        player.openInventory(menu.getInventory());
        openMenus.put(player.getUniqueId().toString(), blockData);
    }

    public void closeMenu(Player player) {
        openMenus.remove(player.getUniqueId().toString());
    }

    /**
     * Sync ability for all players in the chunk immediately after change.
     */
    private void syncAbilityForChunk(Player triggerPlayer) {
        int chunkX = triggerPlayer.getChunk().getX();
        int chunkZ = triggerPlayer.getChunk().getZ();

        for (org.bukkit.entity.Entity entity : triggerPlayer.getChunk().getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                plugin.getChunkManager().forceSyncAbility(player);
            }
        }
    }
}
