package me.devplugins.devModt;

import me.devplugins.devModt.api.DevModtAPI;
import me.devplugins.devModt.commands.CommandHandler;
import me.devplugins.devModt.managers.*;
import me.devplugins.devModt.utils.Scheduler;
import me.devplugins.devModt.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class DevModt extends JavaPlugin {

    private static DevModt instance;
    private ConfigManager configManager;
    private MOTDManager motdManager;
    private EventManager eventManager;
    private PlaceholderManager placeholderManager;
    private DatabaseManager databaseManager;
    private MetricsManager metricsManager;
    private Scheduler scheduler;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;
        
        printStartupBanner();
        
        configManager = new ConfigManager(this);
        placeholderManager = new PlaceholderManager(this);
        databaseManager = new DatabaseManager(this);
        metricsManager = new MetricsManager(this);
        eventManager = new EventManager(this);
        motdManager = new MOTDManager(this);
        scheduler = new Scheduler(this);
        updateChecker = new UpdateChecker(this);
        
        DevModtAPI.initialize(this);
        
        getCommand("devmotd").setExecutor(new CommandHandler(this));
        
        scheduler.startScheduler();
        
        if (configManager.getBoolean("general.check_updates")) {
            updateChecker.checkForUpdates();
        }
        
        getLogger().info("DevModt v" + getDescription().getVersion() + " habilitado com sucesso!");
        getLogger().info("Desenvolvido pela DevPlugins - https://discord.gg/A4F9jtGhFU");
    }

    @Override
    public void onDisable() {
        if (scheduler != null) {
            scheduler.stopScheduler();
        }
        
        if (databaseManager != null) {
            databaseManager.updateDailyStatistics();
            databaseManager.closeConnection();
        }
        
        if (metricsManager != null && configManager.isLoggingEnabled()) {
            getLogger().info("Estatísticas finais:");
            getLogger().info("Total de atualizações: " + metricsManager.getTotalUpdates());
            getLogger().info("Tempo médio de processamento: " + metricsManager.getAverageProcessingTime() + "ms");
        }
        
        getLogger().info("DevModt v" + getDescription().getVersion() + " desabilitado com sucesso!");
    }
    
    private void printStartupBanner() {
        getLogger().info("==========================================");
        getLogger().info("           DEVMODT " + getDescription().getVersion());
        getLogger().info("      Sistema Profissional de MOTD");
        getLogger().info("");
        getLogger().info("        Por: DevPlugins Team");
        getLogger().info("     Discord: discord.gg/A4F9jtGhFU");
        getLogger().info("==========================================");
    }
    
    public static DevModt getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MOTDManager getMotdManager() {
        return motdManager;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public MetricsManager getMetricsManager() {
        return metricsManager;
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
    
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}
