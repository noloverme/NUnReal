package com.noloverme.nunreal.ability;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class AbilityApplier {
    private final JavaPlugin plugin;

    public AbilityApplier(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private NamespacedKey getModifierKey(AbilityType ability) {
        return new NamespacedKey(plugin, "ability_" + ability.name().toLowerCase());
    }

    public void applyAbility(Player player, AbilityType ability) {
        if (ability == null || ability == AbilityType.NONE || ability.getAttribute() == null) {
            return;
        }

        removeModifier(player, ability);

        AttributeInstance instance = player.getAttribute(ability.getAttribute());
        if (instance == null) {
            return;
        }

        NamespacedKey key = getModifierKey(ability);
        AttributeModifier modifier = new AttributeModifier(
            key,
            ability.getValue(),
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.ANY
        );

        instance.addModifier(modifier);
    }

    public void removeModifier(Player player, AbilityType ability) {
        if (ability == null || ability == AbilityType.NONE || ability.getAttribute() == null) {
            return;
        }

        AttributeInstance instance = player.getAttribute(ability.getAttribute());
        if (instance == null) {
            return;
        }

        NamespacedKey key = getModifierKey(ability);
        instance.removeModifier(key);
    }

    public void removeAllModifiers(Player player) {
        for (AbilityType ability : AbilityType.values()) {
            if (ability == AbilityType.NONE || ability.getAttribute() == null) {
                continue;
            }
            removeModifier(player, ability);
        }
    }

    public boolean hasModifier(Player player, AbilityType ability) {
        if (ability == null || ability == AbilityType.NONE || ability.getAttribute() == null) {
            return false;
        }

        AttributeInstance instance = player.getAttribute(ability.getAttribute());
        if (instance == null) {
            return false;
        }

        NamespacedKey key = getModifierKey(ability);
        return instance.getModifier(key) != null;
    }
}
