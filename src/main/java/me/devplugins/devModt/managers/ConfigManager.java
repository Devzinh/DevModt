package me.devplugins.devModt.managers;

import me.devplugins.devModt.DevModt;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    
    private final DevModt plugin;
    private FileConfiguration config;
    
    public ConfigManager(DevModt plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        if (getBoolean("general.enable_logging")) {
            plugin.getLogger().info("Configuração recarregada com sucesso!");
        }
    }
    
    public String getString(String path) {
        return config.getString(path, "");
    }
    
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
    
    public int getInt(String path) {
        return config.getInt(path, 0);
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }
    
    public void set(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }
    
    public boolean isLoggingEnabled() {
        return getBoolean("general.enable_logging");
    }
    
    public boolean isTimeBasedEnabled() {
        return getBoolean("general.time_based_enabled");
    }
    
    public boolean isEventsEnabled() {
        return getBoolean("general.events_enabled");
    }
    
    public String getTimeMessage(String timeRange) {
        return getString("time_messages." + timeRange);
    }
    
    public String getTimeRange(String timeRange) {
        return getString("time_ranges." + timeRange);
    }
    
    public String getEventMessage(String eventName) {
        return getString("events." + eventName + ".message");
    }
    
    public String getEventStartDate(String eventName) {
        return getString("events." + eventName + ".start_date");
    }
    
    public String getEventEndDate(String eventName) {
        return getString("events." + eventName + ".end_date");
    }
    
    public String getCustomEventName() {
        return getString("current_custom_event.name");
    }
    
    public String getCustomEventMessage() {
        return getString("current_custom_event.message");
    }
    
    public boolean isCustomEventActive() {
        return getBoolean("current_custom_event.active");
    }
    
    public void setCustomEvent(String name, String message) {
        setCustomEvent(name, message, "Console");
    }
    
    public void setCustomEvent(String name, String message, String createdBy) {
        set("current_custom_event.name", name);
        set("current_custom_event.message", message);
        set("current_custom_event.active", true);
        
        if (plugin.getDatabaseManager() != null) {
            plugin.getDatabaseManager().saveCustomEvent(name, message, createdBy);
        }
    }
    
    public void clearCustomEvent() {
        set("current_custom_event.name", "");
        set("current_custom_event.message", "");
        set("current_custom_event.active", false);
    }
    
    public int getCacheDuration() {
        return getInt("performance.cache_duration_seconds");
    }
    
    public boolean isAsyncProcessingEnabled() {
        return getBoolean("performance.async_processing");
    }
    
    public int getMaxCacheSize() {
        return getInt("performance.max_cache_size");
    }
    
    public int getCustomEventPriority() {
        return getInt("priorities.custom_event");
    }
    
    public int getSpecialEventPriority() {
        return getInt("priorities.special_event");
    }
    
    public int getWeatherPriority() {
        return getInt("priorities.weather");
    }
    
    public int getTimePriority() {
        return getInt("priorities.time");
    }
    

}