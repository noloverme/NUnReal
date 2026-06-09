package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AutoSmeltListener implements Listener {
    private final NUnReal plugin;
    private final Map<Material, Material> oreToIngot = new HashMap<>();

    public AutoSmeltListener(NUnReal plugin) {
        this.plugin = plugin;
        initializeOreMap();
    }

    private void initializeOreMap() {
        oreToIngot.put(Material.IRON_ORE, Material.IRON_INGOT);
        oreToIngot.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
        oreToIngot.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        oreToIngot.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
        oreToIngot.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        oreToIngot.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (!oreToIngot.containsKey(blockType)) {
            return;
        }

        String world = block.getWorld().getName();
        int chunkX = block.getChunk().getX();
        int chunkZ = block.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null || blockData.getAbility() != AbilityType.AUTO_SMELT) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Check for Silk Touch
        if (plugin.getConfigManager().getBoolean("abilities.AUTO_SMELT.respect-silk-touch", true)) {
            if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }
        }

        event.setDropItems(false);

        Material ingotType = oreToIngot.get(blockType);
        int amount = 1;

        // Calculate Fortune bonus
        if (plugin.getConfigManager().getBoolean("abilities.AUTO_SMELT.respect-fortune", true)) {
            if (tool.containsEnchantment(Enchantment.FORTUNE)) {
                int fortuneLevel = tool.getEnchantmentLevel(Enchantment.FORTUNE);
                int random = (int) (Math.random() * (fortuneLevel + 2));
                amount = Math.max(1, random);
            }
        }

        ItemStack drop = new ItemStack(ingotType, amount);
        block.getWorld().dropItemNaturally(block.getLocation(), drop);
    }
}
