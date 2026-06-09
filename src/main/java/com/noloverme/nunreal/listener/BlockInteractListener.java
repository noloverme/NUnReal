package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.block.CommandBlockData;
import com.noloverme.nunreal.menu.MenuListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInteractListener implements Listener {
    private final NUnReal plugin;
    private MenuListener menuListener;

    public BlockInteractListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    public void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null || clickedBlock.getType() != Material.COMMAND_BLOCK) {
            return;
        }

        if (!event.getAction().toString().contains("RIGHT")) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("nunreal.use")) {
            return;
        }

        String world = clickedBlock.getWorld().getName();
        int chunkX = clickedBlock.getChunk().getX();
        int chunkZ = clickedBlock.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null) {
            return;
        }

        event.setCancelled(true);

        if (menuListener != null) {
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                menuListener.openMenu(player, blockData);
            });
        }
    }
}

