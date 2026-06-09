package com.noloverme.nunreal.chunk;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized chunk state management.
 * Handles ability syncing when players move between chunks.
 */
public class ChunkManager {
    private final NUnReal plugin;
    private final ConcurrentHashMap<UUID, String> playerChunkState = new ConcurrentHashMap<>();

    public ChunkManager(NUnReal plugin) {
        this.plugin = plugin;
    }

    /**
     * Sync player abilities based on current chunk.
     * Called from chunk-crossing detection only (optimized).
     *
     * @return true if player moved to a new chunk
     */
    public boolean syncPlayerChunk(Player player) {
        if (player.getGameMode().toString().equals("CREATIVE") ||
            player.getGameMode().toString().equals("SPECTATOR")) {
            return false;
        }

        UUID playerId = player.getUniqueId();
        String currentChunk = getChunkKey(player);
        String lastChunk = playerChunkState.get(playerId);

        // Check if chunk actually changed
        if (currentChunk.equals(lastChunk)) {
            return false;
        }

        playerChunkState.put(playerId, currentChunk);
        applyChunkAbility(player);
        return true;
    }

    /**
     * Force immediate ability sync (used when ability selection changes).
     */
    public void forceSyncAbility(Player player) {
        applyChunkAbility(player);
    }

    /**
     * Apply ability from command block in current chunk.
     * Removes abilities if no block found or block no longer exists.
     */
    private void applyChunkAbility(Player player) {
        String world = player.getWorld().getName();
        int chunkX = player.getChunk().getX();
        int chunkZ = player.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData != null && isBlockValid(blockData)) {
            AbilityType ability = blockData.getAbility();
            if (ability != AbilityType.NONE && ability.isAttributeAbility()) {
                plugin.getAbilityManager().applyAbility(player, ability);
            } else {
                plugin.getAbilityManager().removeAllAbilities(player);
            }
        } else {
            if (blockData != null) {
                plugin.getBlockManager().removeBlock(blockData.getKey());
            }
            plugin.getAbilityManager().removeAllAbilities(player);
        }
    }

    /**
     * Verify command block still exists at stored location.
     */
    private boolean isBlockValid(CommandBlockData data) {
        try {
            org.bukkit.World world = Bukkit.getWorld(data.getWorld());
            if (world == null) {
                return false;
            }

            org.bukkit.block.Block block = world.getBlockAt(data.getX(), data.getY(), data.getZ());
            return block.getType() == Material.COMMAND_BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate unique chunk identifier.
     */
    private String getChunkKey(Player player) {
        return player.getWorld().getName() + ":" +
               player.getChunk().getX() + ":" +
               player.getChunk().getZ();
    }

    /**
     * Clean up player data on logout.
     */
    public void removePlayer(UUID uuid) {
        playerChunkState.remove(uuid);
    }

    /**
     * Clear all data on plugin disable.
     */
    public void clear() {
        playerChunkState.clear();
    }
}
