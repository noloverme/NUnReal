package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.block.CommandBlockData;
import com.noloverme.nunreal.craft.CommandBlockRecipe;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class BlockDamageListener implements Listener {
    private final NUnReal plugin;

    public BlockDamageListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.COMMAND_BLOCK) {
            return;
        }

        String world = block.getWorld().getName();
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("nunreal.break")) {
            event.setCancelled(true);
            return;
        }

        boolean requirePickaxe = plugin.getConfigManager().getBoolean("blocks.require-pickaxe", true);
        if (requirePickaxe) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            if (tool.getType() == Material.AIR || !tool.getType().toString().contains("PICKAXE")) {
                event.setCancelled(true);
                String message = plugin.getConfigManager().getString("blocks.messages.need-pickaxe", "§cИспользуйте кирку для разрушения!");
                player.sendMessage(message);
                return;
            }
        }

        event.setInstaBreak(true);

        block.setType(Material.AIR);

        ItemStack drop = CommandBlockRecipe.createCommandBlock(plugin);
        block.getWorld().dropItemNaturally(block.getLocation(), drop);

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);

        plugin.getBlockManager().removeBlock(blockData.getKey());

        for (org.bukkit.entity.Player onlinePlayer : block.getWorld().getPlayers()) {
            if (onlinePlayer.getChunk().getX() == chunkX && onlinePlayer.getChunk().getZ() == chunkZ) {
                plugin.getAbilityManager().removeAllAbilities(onlinePlayer);
            }
        }

        String message = plugin.getConfigManager().getString("blocks.messages.broken", "§eБлок способностей разрушен.");
        player.sendMessage(message);
    }
}
