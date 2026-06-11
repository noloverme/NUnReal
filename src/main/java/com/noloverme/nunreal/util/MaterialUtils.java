package com.noloverme.nunreal.util;

import org.bukkit.Material;

public class MaterialUtils {
    public static Material getMaterial(String materialName) {
        if (materialName == null || materialName.isEmpty()) {
            return null;
        }

        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Material getMaterialOrThrow(String materialName) throws IllegalArgumentException {
        if (materialName == null || materialName.isEmpty()) {
            throw new IllegalArgumentException("Material name cannot be null or empty");
        }

        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid material: " + materialName, e);
        }
    }
}
