package com.noloverme.nunreal.ability;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized ability management with thread-safe modifier operations.
 * Ensures atomic ability swaps and proper cleanup.
 */
public class AbilityManager {
    private final JavaPlugin plugin;
    private final ConcurrentHashMap<UUID, AbilityType> playerCurrentAbility = new ConcurrentHashMap<>();

    public AbilityManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Apply ability to player with atomic swap (remove old + add new).
     * Prevents ability overlap during rapid switches.
     */
    public void applyAbility(Player player, AbilityType newAbility) {
        if (newAbility == null || newAbility == AbilityType.NONE || !newAbility.isAttributeAbility()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        AbilityType oldAbility = playerCurrentAbility.get(playerId);

        if (oldAbility == newAbility) {
            return;
        }

        if (oldAbility != null && oldAbility != AbilityType.NONE) {
            removeModifier(player, oldAbility);
        }

        addModifier(player, newAbility);
        playerCurrentAbility.put(playerId, newAbility);
    }

    /**
     * Remove all modifiers and reset player ability state.
     */
    public void removeAllAbilities(Player player) {
        UUID playerId = player.getUniqueId();
        AbilityType currentAbility = playerCurrentAbility.remove(playerId);

        if (currentAbility != null && currentAbility != AbilityType.NONE) {
            removeModifier(player, currentAbility);
        }
    }

    /**
     * Check if player has specific ability applied.
     */
    public boolean hasAbility(Player player, AbilityType ability) {
        if (ability == null || ability == AbilityType.NONE || !ability.isAttributeAbility()) {
            return false;
        }

        return playerCurrentAbility.getOrDefault(player.getUniqueId(), AbilityType.NONE) == ability;
    }

    /**
     * Get current ability applied to player.
     */
    public AbilityType getCurrentAbility(Player player) {
        return playerCurrentAbility.getOrDefault(player.getUniqueId(), AbilityType.NONE);
    }

    private void addModifier(Player player, AbilityType ability) {
        AttributeInstance instance = player.getAttribute(ability.getAttribute());
        if (instance == null) {
            return;
        }

        String modifierName = "nunreal:" + ability.name().toLowerCase();
        AttributeModifier modifier = new AttributeModifier(
            UUID.nameUUIDFromBytes(modifierName.getBytes()),
            modifierName,
            ability.getValue(),
            AttributeModifier.Operation.ADD_NUMBER
        );

        instance.addModifier(modifier);
    }

    private void removeModifier(Player player, AbilityType ability) {
        AttributeInstance instance = player.getAttribute(ability.getAttribute());
        if (instance == null) {
            return;
        }

        String modifierName = "nunreal:" + ability.name().toLowerCase();
        UUID modifierId = UUID.nameUUIDFromBytes(modifierName.getBytes());

        instance.removeModifier(modifierId);
    }

    /**
     * Clean up player data on logout.
     */
    public void removePlayer(UUID uuid) {
        playerCurrentAbility.remove(uuid);
    }

    /**
     * Clear all data on plugin disable.
     */
    public void clear() {
        playerCurrentAbility.clear();
    }
}
