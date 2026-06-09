package com.noloverme.nunreal.ability;

import org.bukkit.attribute.Attribute;

public enum AbilityType {
    NONE("Нет", null, 0.0),
    MINING("Копание", Attribute.BLOCK_BREAK_SPEED, 30.0),
    LONG_ARM("Длиннорукий", Attribute.ENTITY_INTERACTION_RANGE, 15.0),
    ANTI_KNOCKBACK("АнтиОтдача", Attribute.KNOCKBACK_RESISTANCE, 30.0),
    LUCK("Лудоман", Attribute.LUCK, 30.0),
    DWARF("Карлик", Attribute.SCALE, -0.75),
    FALL_RESIST("Мягкое падение", Attribute.SAFE_FALL_DISTANCE, 10.0),
    GROWTH("Рост", null, 0.0),
    MAGNET("Магнит предметов", null, 0.0),
    FLIGHT("Полёт", null, 0.0),
    INFINITE_TRADES("Бесконечные трейды", null, 0.0),
    KEEP_RESOURCES("Сохранение ресурсов", null, 0.0),
    SATURATION("Сытость", null, 0.0),
    AUTO_SMELT("Авто-плавка", null, 0.0),
    HOLY_GROUND("Святая земля", null, 0.0),
    THRIFTY_CRAFT("Бережливый крафт", null, 0.0);

    private final String displayName;
    private final Attribute attribute;
    private final double value;

    AbilityType(String displayName, Attribute attribute, double value) {
        this.displayName = displayName;
        this.attribute = attribute;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getValue() {
        return value;
    }

    public boolean isAttributeAbility() {
        return attribute != null;
    }

    public static AbilityType fromString(String name) {
        try {
            if ("FAST_SMELT".equals(name)) {
                return NONE;
            }
            return AbilityType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
