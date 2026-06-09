package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final NUnReal plugin;

    public PlayerQuitListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getAbilityManager().removeAllAbilities(player);
        plugin.getChunkManager().removePlayer(player.getUniqueId());
        plugin.getFlightManager().removePlayer(player.getUniqueId());
    }
}
