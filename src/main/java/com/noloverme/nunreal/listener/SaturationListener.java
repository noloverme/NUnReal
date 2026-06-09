package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SaturationListener implements Listener {
    private final NUnReal plugin;

    public SaturationListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        String world = player.getWorld().getName();
        int chunkX = player.getChunk().getX();
        int chunkZ = player.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData != null && blockData.getAbility() == AbilityType.SATURATION) {
            event.setCancelled(true);
        }
    }
}
