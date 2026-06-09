package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ThriftyCraftListener implements Listener {
    private final NUnReal plugin;

    public ThriftyCraftListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        CraftingInventory inventory = event.getInventory();

        String world = player.getWorld().getName();
        int chunkX = player.getChunk().getX();
        int chunkZ = player.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData == null || blockData.getAbility() != AbilityType.THRIFTY_CRAFT) {
            return;
        }

        double chance = plugin.getConfigManager().getDouble("abilities.THRIFTY_CRAFT.chance", 0.15);

        if (Math.random() > chance) {
            return;
        }

        List<ItemStack> ingredients = new ArrayList<>();
        for (ItemStack item : inventory.getMatrix()) {
            if (item != null && item.getType() != Material.AIR) {
                ingredients.add(item.clone());
            }
        }

        if (ingredients.isEmpty()) {
            return;
        }

        ItemStack returnedIngredient = ingredients.get((int) (Math.random() * ingredients.size())).clone();
        returnedIngredient.setAmount(1);

        List<String> blacklist = plugin.getConfigManager().getStringList("abilities.THRIFTY_CRAFT.blacklisted-items");
        if (blacklist.contains(returnedIngredient.getType().toString())) {
            return;
        }

        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(returnedIngredient);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), returnedIngredient);
        }

        String message = plugin.getConfigManager().getString("messages.thrifty-craft-triggered",
            "§aБережливый крафт! Вам вернулся 1x {item}.");
        message = message.replace("{item}", returnedIngredient.getType().toString().toLowerCase());
        player.sendMessage(message);
    }
}
