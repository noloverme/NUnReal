package com.noloverme.nunreal.task;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Magnet ability task - pulls nearby items towards the closest player with MAGNET ability.
 * If multiple players are equally close, randomly selects one.
 */
public class MagnetTask extends BukkitRunnable {
    private final NUnReal plugin;
    private final Random random = new Random();

    public MagnetTask(NUnReal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (CommandBlockData blockData : plugin.getBlockManager().getAllBlocks()) {
            if (blockData.getAbility() != AbilityType.MAGNET) {
                continue;
            }

            org.bukkit.World world = Bukkit.getWorld(blockData.getWorld());
            if (world == null || !world.isChunkLoaded(blockData.getChunkX(), blockData.getChunkZ())) {
                continue;
            }

            org.bukkit.Chunk chunk = world.getChunkAt(blockData.getChunkX(), blockData.getChunkZ());
            double radius = plugin.getConfigManager().getDouble("abilities.MAGNET.radius", 8.0);
            double speed = plugin.getConfigManager().getDouble("abilities.MAGNET.speed", 0.30);
            int maxItems = plugin.getConfigManager().getInt("abilities.MAGNET.max-items-per-tick", 100);

            List<Player> playersWithMagnet = getPlayersWithMagnet(chunk);

            if (playersWithMagnet.isEmpty()) {
                continue;
            }

            int processed = 0;
            for (Entity entity : chunk.getEntities()) {
                if (processed >= maxItems) {
                    break;
                }

                if (!(entity instanceof Item)) {
                    continue;
                }

                Item itemEntity = (Item) entity;
                Player targetPlayer = findClosestPlayer(itemEntity, playersWithMagnet, radius);

                if (targetPlayer == null) {
                    continue;
                }

                Vector direction = targetPlayer.getLocation().toVector()
                    .subtract(itemEntity.getLocation().toVector())
                    .normalize()
                    .multiply(speed);

                itemEntity.setVelocity(direction);
                processed++;
            }
        }
    }

    /**
     * Get all players in chunk with MAGNET ability active.
     */
    private List<Player> getPlayersWithMagnet(org.bukkit.Chunk chunk) {
        List<Player> players = new ArrayList<>();

        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            Player player = (Player) entity;

            if (player.getGameMode().toString().equals("SPECTATOR") ||
                player.getGameMode().toString().equals("CREATIVE")) {
                continue;
            }

            if (plugin.getAbilityManager().getCurrentAbility(player) == AbilityType.MAGNET) {
                players.add(player);
            }
        }

        return players;
    }

    /**
     * Find closest player to item within radius.
     * If multiple players at same distance (within 0.1 blocks), randomly select one.
     */
    private Player findClosestPlayer(Item itemEntity, List<Player> players, double radius) {
        double closestDistance = Double.MAX_VALUE;
        List<Player> closestPlayers = new ArrayList<>();

        for (Player player : players) {
            double distance = itemEntity.getLocation().distance(player.getLocation());

            if (distance < 0.5 || distance > radius) {
                continue;
            }

            if (distance < closestDistance - 0.1) {
                closestDistance = distance;
                closestPlayers.clear();
                closestPlayers.add(player);
            } else if (Math.abs(distance - closestDistance) <= 0.1) {
                closestPlayers.add(player);
            }
        }

        if (closestPlayers.isEmpty()) {
            return null;
        }

        return closestPlayers.get(random.nextInt(closestPlayers.size()));
    }
}
