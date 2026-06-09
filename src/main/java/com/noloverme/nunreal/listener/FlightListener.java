package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FlightListener implements Listener {
    private final NUnReal plugin;

    public FlightListener(NUnReal plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getChunk() == event.getTo().getChunk()) {
            return;
        }

        syncFlightState(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        syncFlightState(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        syncFlightState(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        syncFlightState(event.getPlayer());
    }

    private void syncFlightState(Player player) {
        if (player.getGameMode().toString().equals("CREATIVE") ||
            player.getGameMode().toString().equals("SPECTATOR")) {
            return;
        }

        String world = player.getWorld().getName();
        int chunkX = player.getChunk().getX();
        int chunkZ = player.getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData != null && blockData.getAbility() == AbilityType.FLIGHT) {
            if (!plugin.getFlightManager().hasFlightFromPlugin(player)) {
                double flySpeed = plugin.getConfigManager().getDouble("abilities.FLIGHT.fly-speed", 0.05);
                plugin.getFlightManager().grantFlight(player, flySpeed);
            }
        } else {
            if (plugin.getFlightManager().hasFlightFromPlugin(player)) {
                int slowFallingSeconds = plugin.getConfigManager().getInt("abilities.FLIGHT.slow-falling-seconds", 5);
                plugin.getFlightManager().revokeFlight(player, slowFallingSeconds);
            }
        }
    }
}
