package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final NUnReal plugin;

    public PlayerDeathListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String world = player.getWorld().getName();
        int chunkX = player.getChunk().getX();
        int chunkZ = player.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null || blockData.getAbility() != AbilityType.KEEP_RESOURCES) {
            return;
        }

        if (!isBlockValid(blockData)) {
            return;
        }

        boolean keepInventory = plugin.getConfigManager().getBoolean("abilities.KEEP_RESOURCES.keep-inventory", true);
        boolean keepExp = plugin.getConfigManager().getBoolean("abilities.KEEP_RESOURCES.keep-exp", true);

        if (keepInventory) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }

        if (keepExp) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }

        String message = plugin.getConfigManager().getString("messages.resources-kept", "§6Ваши ресурсы сохранены благодаря способности чанка.");
        player.sendMessage(message);
    }

    private boolean isBlockValid(CommandBlockData data) {
        try {
            org.bukkit.World world = org.bukkit.Bukkit.getWorld(data.getWorld());
            if (world == null) {
                return false;
            }

            org.bukkit.block.Block block = world.getBlockAt(data.getX(), data.getY(), data.getZ());
            return block.getType() == Material.COMMAND_BLOCK;
        } catch (Exception e) {
            return false;
        }
    }
}
