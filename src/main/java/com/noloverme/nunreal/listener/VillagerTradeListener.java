package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.MerchantInventory;

public class VillagerTradeListener implements Listener {
    private final NUnReal plugin;

    public VillagerTradeListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMerchantInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory)) {
            return;
        }

        MerchantInventory merchantInventory = (MerchantInventory) event.getInventory();
        if (!(merchantInventory.getMerchant() instanceof Villager)) {
            return;
        }

        Villager villager = (Villager) merchantInventory.getMerchant();
        String world = villager.getWorld().getName();
        int chunkX = villager.getChunk().getX();
        int chunkZ = villager.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null || blockData.getAbility() != AbilityType.INFINITE_TRADES) {
            return;
        }

        if (!isBlockValid(blockData)) {
            return;
        }

        int selectedRecipeIndex = merchantInventory.getSelectedRecipeIndex();
        if (selectedRecipeIndex < 0 || selectedRecipeIndex >= villager.getRecipes().size()) {
            return;
        }

        org.bukkit.inventory.MerchantRecipe recipe = villager.getRecipes().get(selectedRecipeIndex);

        if (recipe.getUses() >= recipe.getMaxUses()) {
            recipe.setUses(0);
        }
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
