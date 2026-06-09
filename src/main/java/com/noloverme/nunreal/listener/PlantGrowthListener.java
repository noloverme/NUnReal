package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class PlantGrowthListener implements Listener {
    private final NUnReal plugin;

    public PlantGrowthListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        org.bukkit.block.data.BlockData data = block.getBlockData();

        if (!(data instanceof Ageable)) {
            return;
        }

        String world = block.getWorld().getName();
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null || blockData.getAbility() != AbilityType.GROWTH) {
            return;
        }

        double chance = plugin.getConfigManager().getDouble("abilities.GROWTH.extra-growth-chance", 0.5);

        if (Math.random() < chance) {
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Block currentBlock = block.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ());

                if (currentBlock.getType() != block.getType()) {
                    return;
                }

                org.bukkit.block.data.BlockData currentData = currentBlock.getBlockData();
                if (!(currentData instanceof Ageable)) {
                    return;
                }

                Ageable ageable = (Ageable) currentData;
                CommandBlockData currentBlockData = plugin.getBlockManager().getBlockInChunk(
                    currentBlock.getWorld().getName(),
                    currentBlock.getChunk().getX(),
                    currentBlock.getChunk().getZ()
                );

                if (currentBlockData == null || currentBlockData.getAbility() != AbilityType.GROWTH) {
                    return;
                }

                if (ageable.getAge() < ageable.getMaximumAge()) {
                    ageable.setAge(ageable.getAge() + 1);
                    currentBlock.setBlockData(ageable, false);
                }
            }, 1);
        }
    }
}
