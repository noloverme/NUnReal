package com.noloverme.nunreal.task;

import com.noloverme.nunreal.NUnReal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Periodic chunk sync task.
 * Delegates to ChunkManager for centralized chunk state management.
 */
public class ChunkCheckTask extends BukkitRunnable {
    private final NUnReal plugin;

    public ChunkCheckTask(NUnReal plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getChunkManager().syncPlayerChunk(player);
        }
    }
}

