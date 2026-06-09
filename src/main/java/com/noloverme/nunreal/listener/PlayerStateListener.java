package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Chunk boundary detection via player movement.
 * Only syncs when player crosses chunk borders (optimized for performance).
 */
public class PlayerStateListener implements Listener {
    private final NUnReal plugin;
    private final ConcurrentHashMap<UUID, String> lastChunk = new ConcurrentHashMap<>();

    public PlayerStateListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        String fromChunk = event.getFrom().getChunk().getX() + ":" + event.getFrom().getChunk().getZ();
        String toChunk = event.getTo().getChunk().getX() + ":" + event.getTo().getChunk().getZ();

        // Only sync if chunk actually changed
        if (!fromChunk.equals(toChunk)) {
            lastChunk.put(playerId, toChunk);
            plugin.getChunkManager().syncPlayerChunk(player);
        }
    }

    public void removePlayer(UUID uuid) {
        lastChunk.remove(uuid);
    }

    public void clear() {
        lastChunk.clear();
    }
}
