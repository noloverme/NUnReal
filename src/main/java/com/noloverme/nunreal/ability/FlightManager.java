package com.noloverme.nunreal.ability;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FlightManager {
    private final Set<UUID> playersWithFlightByPlugin = new HashSet<>();

    public void grantFlight(Player player, double flySpeed) {
        if (playersWithFlightByPlugin.contains(player.getUniqueId())) {
            return;
        }

        player.setAllowFlight(true);
        player.setFlySpeed((float) flySpeed);
        playersWithFlightByPlugin.add(player.getUniqueId());
    }

    public void revokeFlight(Player player, int slowFallingSeconds) {
        UUID uuid = player.getUniqueId();

        if (!playersWithFlightByPlugin.contains(uuid)) {
            return;
        }

        playersWithFlightByPlugin.remove(uuid);

        if (player.getGameMode().toString().equals("CREATIVE") ||
            player.getGameMode().toString().equals("SPECTATOR")) {
            return;
        }

        player.setAllowFlight(false);
        player.setFlying(false);

        if (slowFallingSeconds > 0) {
            player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_FALLING,
                slowFallingSeconds * 20,
                0,
                true,
                false
            ));
        }
    }

    public boolean hasFlightFromPlugin(Player player) {
        return playersWithFlightByPlugin.contains(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        playersWithFlightByPlugin.remove(uuid);
    }

    public void clear() {
        playersWithFlightByPlugin.clear();
    }
}
