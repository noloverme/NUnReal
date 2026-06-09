package com.noloverme.nunreal.listener;

import com.noloverme.nunreal.NUnReal;
import com.noloverme.nunreal.ability.AbilityType;
import com.noloverme.nunreal.block.CommandBlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HolyGroundListener implements Listener {
    private final NUnReal plugin;
    private final Set<CreatureSpawnEvent.SpawnReason> blockedReasons = new HashSet<>();
    private final Set<EntityType> hostileMobs = new HashSet<>();

    public HolyGroundListener(NUnReal plugin) {
        this.plugin = plugin;
        initializeBlockedReasons();
        initializeHostileMobs();
    }

    private void initializeBlockedReasons() {
        List<String> reasons = plugin.getConfigManager().getStringList("abilities.HOLY_GROUND.blocked-spawn-reasons");
        for (String reason : reasons) {
            try {
                blockedReasons.add(CreatureSpawnEvent.SpawnReason.valueOf(reason));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void initializeHostileMobs() {
        hostileMobs.add(EntityType.ZOMBIE);
        hostileMobs.add(EntityType.ZOMBIE_VILLAGER);
        hostileMobs.add(EntityType.DROWNED);
        hostileMobs.add(EntityType.HUSK);
        hostileMobs.add(EntityType.SKELETON);
        hostileMobs.add(EntityType.WITHER_SKELETON);
        hostileMobs.add(EntityType.STRAY);
        hostileMobs.add(EntityType.CREEPER);
        hostileMobs.add(EntityType.SPIDER);
        hostileMobs.add(EntityType.CAVE_SPIDER);
        hostileMobs.add(EntityType.ENDERMAN);
        hostileMobs.add(EntityType.WITCH);
        hostileMobs.add(EntityType.SLIME);
        hostileMobs.add(EntityType.PHANTOM);
        hostileMobs.add(EntityType.PILLAGER);
        hostileMobs.add(EntityType.VINDICATOR);
        hostileMobs.add(EntityType.EVOKER);
        hostileMobs.add(EntityType.VEX);
        hostileMobs.add(EntityType.RAVAGER);
        hostileMobs.add(EntityType.ILLUSIONER);
        hostileMobs.add(EntityType.WARDEN);
        hostileMobs.add(EntityType.BREEZE);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!blockedReasons.contains(event.getSpawnReason())) {
            return;
        }

        if (!hostileMobs.contains(event.getEntityType())) {
            return;
        }

        String world = event.getLocation().getWorld().getName();
        int chunkX = event.getLocation().getChunk().getX();
        int chunkZ = event.getLocation().getChunk().getZ();

        CommandBlockData blockData = plugin.getBlockManager().getBlockInChunk(world, chunkX, chunkZ);

        if (blockData != null && blockData.getAbility() == AbilityType.HOLY_GROUND) {
            event.setCancelled(true);
        }
    }
}
