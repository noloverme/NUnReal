package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class PistonListener implements Listener {
    private final NUnReal plugin;

    public PistonListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            if (isAbilityBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            if (isAbilityBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private boolean isAbilityBlock(Block block) {
        String world = block.getWorld().getName();
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        return blockData != null && blockData.getX() == block.getX() &&
               blockData.getY() == block.getY() && blockData.getZ() == block.getZ();
    }
}
