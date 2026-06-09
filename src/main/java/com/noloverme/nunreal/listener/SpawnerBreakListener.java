package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.spawner.SpawnerItemFactory;
import com.noloverme.nunreal.spawner.SpawnerType;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Handles breaking of spawner blocks and drops custom spawner items with Silk Touch.
 */
public class SpawnerBreakListener implements Listener {
    private final NUnReal plugin;

    public SpawnerBreakListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        // Check if block is a spawner
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }

        Player player = event.getPlayer();

        // Check permission
        if (!player.hasPermission("nunreal.spawner.break")) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getString("messages.no-permission", "§cНет прав!"));
            return;
        }

        // Get spawner block
        org.bukkit.block.BlockState blockState = event.getBlock().getState();
        if (!(blockState instanceof CreatureSpawner)) {
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) blockState;

        EntityType spawnedType = spawner.getSpawnedType();
        if (spawnedType == null || spawnedType == EntityType.UNKNOWN) {
            return;
        }

        // Check for Silk Touch
        ItemStack tool = player.getInventory().getItemInMainHand();
        ItemMeta meta = tool.getItemMeta();
        boolean hasSilkTouch = meta != null && meta.hasEnchant(Enchantment.SILK_TOUCH) && meta.getEnchantLevel(Enchantment.SILK_TOUCH) > 0;

        if (!hasSilkTouch) {
            return; // Allow normal drop
        }

        // Convert EntityType to SpawnerType
        SpawnerType type = SpawnerType.fromEntityType(spawnedType);
        if (type == null) {
            return; // Not a supported spawner type
        }

        // Check if recipe is enabled
        if (!type.isEnabled(plugin.getConfigManager())) {
            return; // Recipe disabled, allow normal drop
        }

        // Create custom spawner item
        ItemStack spawnerItem = SpawnerItemFactory.createSpawnerItem(type, plugin.getConfigManager());
        if (spawnerItem == null) {
            return;
        }

        // Cancel default drop and drop custom item
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), spawnerItem);

        // Send success message
        String message = plugin.getConfigManager().getString("spawners.messages.spawner-broken", "§eСпавнер {mob} получен.");
        message = message.replace("{mob}", type.getDisplayName(plugin.getConfigManager()));
        player.sendMessage(message);
    }
}
