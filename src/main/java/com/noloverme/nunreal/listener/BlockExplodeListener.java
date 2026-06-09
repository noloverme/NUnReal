package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class BlockExplodeListener implements Listener {
    private final NUnReal plugin;

    public BlockExplodeListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        for (Block block : blocks) {
            checkAndRemoveBlock(block);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        for (Block block : blocks) {
            checkAndRemoveBlock(block);
        }
    }

    private void checkAndRemoveBlock(Block block) {
        String world = block.getWorld().getName();
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData != null && blockData.getX() == block.getX() &&
            blockData.getY() == block.getY() && blockData.getZ() == block.getZ()) {

            plugin.getBlockManager().removeBlock(blockData.getKey());

            for (org.bukkit.entity.Player player : block.getWorld().getPlayers()) {
                if (player.getChunk().getX() == chunkX && player.getChunk().getZ() == chunkZ) {
                    plugin.getAbilityManager().removeAllAbilities(player);
                }
            }
        }
    }
}
