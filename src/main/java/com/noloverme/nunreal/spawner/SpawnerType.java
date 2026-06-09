package com.noloverme.nunreal.spawner;

import com.noloverme.nunreal.config.ConfigManager;
import org.bukkit.entity.EntityType;

/**
 * Enum of all supported spawner types with configuration and display information.
 */
public enum SpawnerType {
    SKELETON(EntityType.SKELETON, "Скелет"),
    ZOMBIE(EntityType.ZOMBIE, "Зомби"),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, "Визер-скелет"),
    GHAST(EntityType.GHAST, "Гаст"),
    CREEPER(EntityType.CREEPER, "Крипер"),
    HOGLIN(EntityType.HOGLIN, "Хоглин"),
    SHULKER(EntityType.SHULKER, "Шалкер"),
    SPIDER(EntityType.SPIDER, "Паук"),
    BLAZE(EntityType.BLAZE, "Ифрит"),
    VINDICATOR(EntityType.VINDICATOR, "Поборник"),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, "Магмовый куб"),
    SLIME(EntityType.SLIME, "Слизень"),
    ENDERMAN(EntityType.ENDERMAN, "Эндермен"),
    COW(EntityType.COW, "Корова"),
    MOOSHROOM(EntityType.MOOSHROOM, "Грибная корова"),
    BREEZE(EntityType.BREEZE, "Бриз"),
    SNIFFER(EntityType.SNIFFER, "Сниффер"),
    IRON_GOLEM(EntityType.IRON_GOLEM, "Железный голем"),
    ARMADILLO(EntityType.ARMADILLO, "Броненосец"),
    WOLF(EntityType.WOLF, "Волк"),
    TURTLE(EntityType.TURTLE, "Черепаха"),
    VILLAGER(EntityType.VILLAGER, "Житель"),
    SALMON(EntityType.SALMON, "Лосось"),
    COD(EntityType.COD, "Треска"),
    CAT(EntityType.CAT, "Кот"),
    DROWNED(EntityType.DROWNED, "Утопленник"),
    SNOW_GOLEM(EntityType.SNOW_GOLEM, "Снежный голем");

    private final EntityType entityType;
    private final String displayName;

    SpawnerType(EntityType entityType, String displayName) {
        this.entityType = entityType;
        this.displayName = displayName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this spawner type is enabled in the configuration.
     */
    public boolean isEnabled(ConfigManager config) {
        String path = "spawners.recipes." + this.name() + ".enabled";
        return config.getBoolean(path, true);
    }

    /**
     * Get config key for this spawner type (recipe path).
     */
    public String getRecipeKey() {
        return "spawners.recipes." + this.name();
    }

    /**
     * Convert EntityType to SpawnerType.
     */
    public static SpawnerType fromEntityType(EntityType entityType) {
        if (entityType == null) {
            return null;
        }
        try {
            return SpawnerType.valueOf(entityType.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get display name from config or use default.
     */
    public String getDisplayName(ConfigManager config) {
        String configName = config.getString("spawners.mob-names." + this.name(), null);
        return configName != null ? configName : this.displayName;
    }
}
