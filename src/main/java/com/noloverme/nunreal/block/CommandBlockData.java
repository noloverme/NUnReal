package com.noloverme.nunreal.block;

import com.noloverme.nunreal.ability.AbilityType;

import java.util.UUID;

public class CommandBlockData {
    private String world;
    private int x;
    private int y;
    private int z;
    private int chunkX;
    private int chunkZ;
    private AbilityType ability;
    private UUID ownerUUID;

    public CommandBlockData(String world, int x, int y, int z, int chunkX, int chunkZ, AbilityType ability, UUID ownerUUID) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.ability = ability;
        this.ownerUUID = ownerUUID;
    }

    public String getWorld() { return world; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getChunkX() { return chunkX; }
    public int getChunkZ() { return chunkZ; }
    public AbilityType getAbility() { return ability; }
    public UUID getOwnerUUID() { return ownerUUID; }

    public void setAbility(AbilityType ability) {
        this.ability = ability;
    }

    public String getKey() {
        return world + ":" + chunkX + ":" + chunkZ;
    }
}
