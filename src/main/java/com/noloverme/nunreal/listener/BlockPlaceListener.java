package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import com.noloverme.nunreal.craft.CommandBlockRecipe;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {
    private final NUnReal plugin;

    public BlockPlaceListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Проверяем, что это клик ПКМ
        if (event.getAction().isLeftClick()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        // Проверяем, что в руке кастомный блок способностей
        if (!CommandBlockRecipe.isAbilityBlock(item)) {
            return;
        }

        // Проверяем разрешение
        if (!player.hasPermission("nunreal.place")) {
            event.setCancelled(true);
            String message = plugin.getConfigManager().getString("blocks.messages.no-permission", "§cУ вас нет прав на размещение блока способностей!");
            player.sendMessage(message);
            return;
        }

        // Получаем целевой блок (на который кликнул игрок)
        Block targetBlock = event.getClickedBlock();
        if (targetBlock == null) {
            return;
        }

        // Получаем блок, на котором будет размещен командный блок (выше целевого)
        Block placeBlock = targetBlock.getRelative(event.getBlockFace());

        // Проверяем, что блок можно переписать
        if (!isReplaceable(placeBlock)) {
            String message = plugin.getConfigManager().getString("blocks.messages.cannot-place", "§cЭтот блок нельзя заменить!");
            player.sendMessage(message);
            return;
        }

        String world = placeBlock.getWorld().getName();
        int chunkX = placeBlock.getChunk().getX();
        int chunkZ = placeBlock.getChunk().getZ();

        // Проверяем правило one-per-chunk
        boolean onePerChunk = plugin.getConfigManager().getBoolean("blocks.one-per-chunk", true);
        if (onePerChunk && plugin.getBlockManager().blockExistsInChunk(world, chunkX, chunkZ)) {
            String message = plugin.getConfigManager().getString("blocks.messages.already-exists", "§cВ этом чанке уже есть блок способностей!");
            player.sendMessage(message);
            return;
        }

        // Создаем данные блока
        CommandBlockData data = new CommandBlockData(
            world,
            placeBlock.getX(),
            placeBlock.getY(),
            placeBlock.getZ(),
            chunkX,
            chunkZ,
            AbilityType.NONE,
            player.getUniqueId()
        );

        // Добавляем блок в менеджер
        plugin.getBlockManager().addBlock(data);

        // Устанавливаем командный блок на месте (делаем его функциональным)
        placeBlock.setType(Material.COMMAND_BLOCK);

        // Уменьшаем количество предметов в руке
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().removeItem(item);
        }

        // Отменяем исходное событие
        event.setCancelled(true);

        // Отправляем сообщение
        String message = plugin.getConfigManager().getString("blocks.messages.placed", "§aБлок способностей установлен! Нажмите ПКМ для настройки.");
        player.sendMessage(message);
    }

    /**
     * Проверяет, можно ли заменить блок на командный блок
     */
    private boolean isReplaceable(Block block) {
        Material material = block.getType();
        // Можно заменять воздух и жидкости
        return material == Material.AIR || material.isBlock() && !material.isOccluding();
    }
}
