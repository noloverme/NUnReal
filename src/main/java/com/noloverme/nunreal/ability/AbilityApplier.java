package com.noloverme.nunreal.ability;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AbilityApplier {
    private final JavaPlugin plugin;

    public AbilityApplier(JavaPlugin plugin) {
        this.plugin = plugin;
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

        String modifierName = "nunreal:" + ability.name().toLowerCase();
        AttributeModifier modifier = new AttributeModifier(
            UUID.nameUUIDFromBytes(modifierName.getBytes()),
            modifierName,
            ability.getValue(),
            AttributeModifier.Operation.ADD_NUMBER
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

        String modifierName = "nunreal:" + ability.name().toLowerCase();
        UUID modifierId = UUID.nameUUIDFromBytes(modifierName.getBytes());

        instance.removeModifier(modifierId);
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

        String modifierName = "nunreal:" + ability.name().toLowerCase();
        UUID modifierId = UUID.nameUUIDFromBytes(modifierName.getBytes());

        return instance.getModifiers().stream()
            .anyMatch(m -> m.getUniqueId().equals(modifierId));
    }
}
