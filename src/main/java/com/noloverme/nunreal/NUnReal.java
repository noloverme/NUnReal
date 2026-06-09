package com.noloverme.nunreal;

import com.noloverme.nunreal.ability.AbilityManager;
import com.noloverme.nunreal.ability.FlightManager;
import com.noloverme.nunreal.block.CommandBlockManager;
import com.noloverme.nunreal.chunk.ChunkManager;
import com.noloverme.nunreal.command.NUnRealCommand;
import com.noloverme.nunreal.config.ConfigManager;
import com.noloverme.nunreal.craft.CommandBlockRecipe;
import com.noloverme.nunreal.listener.*;
import com.noloverme.nunreal.menu.MenuListener;
import com.noloverme.nunreal.spawner.SpawnerRecipeManager;
import com.noloverme.nunreal.task.ChunkCheckTask;
import com.noloverme.nunreal.task.MagnetTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NUnReal extends JavaPlugin {
    private static NUnReal instance;
    private ConfigManager configManager;
    private CommandBlockManager blockManager;
    private AbilityManager abilityManager;
    private FlightManager flightManager;
    private ChunkManager chunkManager;
    private SpawnerRecipeManager spawnerRecipeManager;
    private ChunkCheckTask checkTask;
    private MagnetTask magnetTask;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        blockManager = new CommandBlockManager(this);
        blockManager.loadData();

        abilityManager = new AbilityManager(this);
        flightManager = new FlightManager();
        chunkManager = new ChunkManager(this);
        spawnerRecipeManager = new SpawnerRecipeManager(this);

        CommandBlockRecipe.registerRecipe(this);
        spawnerRecipeManager.registerRecipes(this);

        registerListeners();
        registerCommands();

        checkTask = new ChunkCheckTask(this);
        int interval = configManager.getInt("task.interval-ticks", 20);
        checkTask.runTaskTimer(this, 20, interval);

        magnetTask = new MagnetTask(this);
        int magnetInterval = configManager.getInt("abilities.MAGNET.interval-ticks", 5);
        magnetTask.runTaskTimer(this, 20, magnetInterval);

        getLogger().info(configManager.getString("messages.plugin-enabled"));
    }

    @Override
    public void onDisable() {
        if (checkTask != null) {
            checkTask.cancel();
        }

        if (magnetTask != null) {
            magnetTask.cancel();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            abilityManager.removeAllAbilities(player);
            flightManager.revokeFlight(player, 0);
        }

        abilityManager.clear();
        chunkManager.clear();
        flightManager.clear();
        spawnerRecipeManager.unregisterRecipes();
        blockManager.saveData();
        getLogger().info(configManager.getString("messages.plugin-disabled"));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockDamageListener(this), this);

        MenuListener menuListener = new MenuListener(this);
        BlockInteractListener interactListener = new BlockInteractListener(this);
        interactListener.setMenuListener(menuListener);

        Bukkit.getPluginManager().registerEvents(interactListener, this);
        Bukkit.getPluginManager().registerEvents(menuListener, this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockExplodeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PistonListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlantGrowthListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FlightListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerStateListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VillagerTradeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AutoSmeltListener(this), this);
        Bukkit.getPluginManager().registerEvents(new HolyGroundListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SaturationListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ThriftyCraftListener(this), this);
    }

    private void registerCommands() {
        NUnRealCommand cmd = new NUnRealCommand(this);
        getCommand("nunreal").setExecutor(cmd);
        getCommand("nunreal").setTabCompleter(cmd);
    }

    public static NUnReal getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandBlockManager getBlockManager() {
        return blockManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public FlightManager getFlightManager() {
        return flightManager;
    }

    public SpawnerRecipeManager getSpawnerRecipeManager() {
        return spawnerRecipeManager;
    }

}
