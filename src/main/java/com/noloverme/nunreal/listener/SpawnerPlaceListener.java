package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.spawner.SpawnerItemFactory;
import com.noloverme.nunreal.spawner.SpawnerType;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Handles placement of custom spawner items and sets the correct entity type.
 */
public class SpawnerPlaceListener implements Listener {
    private final NUnReal plugin;

    public SpawnerPlaceListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {
        // Check if placed item is a custom spawner
        if (!SpawnerItemFactory.isCustomSpawner(event.getItemInHand())) {
            return;
        }

        Player player = event.getPlayer();

        // Check permission
        if (!player.hasPermission("nunreal.spawner.place")) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getString("messages.no-permission", "§cНет прав!"));
            return;
        }

        // Get spawner type from item
        SpawnerType type = SpawnerItemFactory.getSpawnerType(event.getItemInHand());
        if (type == null) {
            String message = plugin.getConfigManager().getString("spawners.messages.invalid-spawner", "§cОшибка: неизвестный тип спавнера.");
            player.sendMessage(message);
            plugin.getLogger().warning("Failed to get spawner type from item");
            return;
        }

        // Get entity type
        EntityType entityType = type.getEntityType();
        if (entityType == null) {
            String message = plugin.getConfigManager().getString("spawners.messages.invalid-spawner", "§cОшибка: неизвестный тип спавнера.");
            player.sendMessage(message);
            plugin.getLogger().warning("Invalid entity type for spawner: " + type.name());
            return;
        }

        // Apply entity type to spawner block
        try {
            org.bukkit.block.BlockState blockState = event.getBlock().getState();
            if (blockState instanceof CreatureSpawner) {
                CreatureSpawner spawner = (CreatureSpawner) blockState;
                spawner.setSpawnedType(entityType);
                spawner.update();

                // Send success message
                String message = plugin.getConfigManager().getString("spawners.messages.spawner-placed", "§aСпавнер {mob} установлен.");
                message = message.replace("{mob}", type.getDisplayName(plugin.getConfigManager()));
                player.sendMessage(message);
            }
        } catch (Exception e) {
            String message = plugin.getConfigManager().getString("spawners.messages.invalid-spawner", "§cОшибка: неизвестный тип спавнера.");
            player.sendMessage(message);
            plugin.getLogger().severe("Error placing spawner: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
