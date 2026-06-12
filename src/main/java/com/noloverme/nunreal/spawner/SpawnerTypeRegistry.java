package com.noloverme.nunreal.spawner;

import com.noloverme.nunreal.config.ConfigManager;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerTypeRegistry {
    private static final Map<EntityType, SpawnerTypeData> registry = new HashMap<>();

    public static void initialize(ConfigManager config) {
        for (EntityType entityType : EntityType.values()) {
            if (isValidSpawnable(entityType)) {
                String recipePath = "spawners.recipes." + entityType.name();
                boolean hasRecipe = config.getConfigurationSection(recipePath) != null;

                if (hasRecipe) {
                    String displayName = getOrCreateDisplayName(entityType, config);
                    registry.put(entityType, new SpawnerTypeData(entityType, displayName));
                }
            }
        }
    }

    private static boolean isValidSpawnable(EntityType type) {
        try {
            return type.isSpawnable();
        } catch (Exception e) {
            return false;
        }
    }

    private static String getOrCreateDisplayName(EntityType type, ConfigManager config) {
        String configKey = "spawners.mob-names." + type.name();
        String configName = config.getString(configKey, null);

        if (configName != null) {
            return configName;
        }

        String englishName = convertToEnglish(type.name());
        config.setString(configKey, englishName);
        return englishName;
    }

    private static String convertToEnglish(String enumName) {
        return enumName.substring(0, 1).toUpperCase() +
                enumName.substring(1).toLowerCase().replace("_", " ");
    }

    public static SpawnerTypeData get(EntityType type) {
        return registry.get(type);
    }

    public static Collection<SpawnerTypeData> getAll() {
        return new ArrayList<>(registry.values());
    }

    public static boolean isRegistered(EntityType type) {
        return registry.containsKey(type);
    }

    public static class SpawnerTypeData {
        private final EntityType entityType;
        private final String displayName;

        public SpawnerTypeData(EntityType entityType, String displayName) {
            this.entityType = entityType;
            this.displayName = displayName;
        }

        public EntityType getEntityType() {
            return entityType;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEnumName() {
            return entityType.name();
        }
    }
}

