package com.noloverme.nunreal.block;

import com.noloverme.nunreal.ability.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommandBlockManager {
    private final JavaPlugin plugin;
    private final Map<String, CommandBlockData> blocks = new HashMap<>();
    private File dataFile;

    public CommandBlockManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadData() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        if (!config.contains("blocks")) {
            return;
        }

        for (String key : config.getConfigurationSection("blocks").getKeys(false)) {
            String path = "blocks." + key;
            String world = config.getString(path + ".world");
            int x = config.getInt(path + ".x");
            int y = config.getInt(path + ".y");
            int z = config.getInt(path + ".z");
            int chunkX = config.getInt(path + ".chunkX");
            int chunkZ = config.getInt(path + ".chunkZ");
            AbilityType ability = AbilityType.fromString(config.getString(path + ".ability", "NONE"));
            UUID owner = UUID.fromString(config.getString(path + ".owner"));

            CommandBlockData data = new CommandBlockData(world, x, y, z, chunkX, chunkZ, ability, owner);
            blocks.put(key, data);
        }

        plugin.getLogger().info("Loaded " + blocks.size() + " ability blocks");
    }

    public void saveData() {
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, CommandBlockData> entry : blocks.entrySet()) {
            String key = entry.getKey();
            CommandBlockData data = entry.getValue();

            String path = "blocks." + key;
            config.set(path + ".world", data.getWorld());
            config.set(path + ".x", data.getX());
            config.set(path + ".y", data.getY());
            config.set(path + ".z", data.getZ());
            config.set(path + ".chunkX", data.getChunkX());
            config.set(path + ".chunkZ", data.getChunkZ());
            config.set(path + ".ability", data.getAbility().name());
            config.set(path + ".owner", data.getOwnerUUID().toString());
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }

    public void addBlock(CommandBlockData data) {
        blocks.put(data.getKey(), data);
        saveData();
    }

    public void removeBlock(String key) {
        blocks.remove(key);
        saveData();
    }

    public CommandBlockData getBlock(String key) {
        return blocks.get(key);
    }

    public boolean blockExists(String key) {
        return blocks.containsKey(key);
    }

    public boolean blockExistsInChunk(String world, int chunkX, int chunkZ) {
        String key = world + ":" + chunkX + ":" + chunkZ;
        return blocks.containsKey(key);
    }

    public CommandBlockData getBlockInChunk(String world, int chunkX, int chunkZ) {
        String key = world + ":" + chunkX + ":" + chunkZ;
        return blocks.get(key);
    }

    public void updateAbility(String key, AbilityType ability) {
        CommandBlockData data = blocks.get(key);
        if (data != null) {
            data.setAbility(ability);
            saveData();
        }
    }

    public Collection<CommandBlockData> getAllBlocks() {
        return blocks.values();
    }
}
