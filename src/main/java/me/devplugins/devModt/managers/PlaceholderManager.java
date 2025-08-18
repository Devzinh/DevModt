package me.devplugins.devModt.managers;

import me.devplugins.devModt.DevModt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlaceholderManager {
    
    private final DevModt plugin;
    private final Map<String, String> customPlaceholders;
    private final SimpleDateFormat timeFormat;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat fullDateFormat;
    
    public PlaceholderManager(DevModt plugin) {
        this.plugin = plugin;
        this.customPlaceholders = new HashMap<>();
        this.timeFormat = new SimpleDateFormat("HH:mm");
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.fullDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }
    
    public String processPlaceholders(String message) {
        if (message == null || message.isEmpty()) return "";
        
        String processed = message;
        
        processed = processed.replace("{players_online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        processed = processed.replace("{max_players}", String.valueOf(Bukkit.getMaxPlayers()));
        processed = processed.replace("{event_name}", getEventName());
        processed = processed.replace("{time}", timeFormat.format(new Date()));
        processed = processed.replace("{date}", dateFormat.format(new Date()));
        processed = processed.replace("{full_date}", fullDateFormat.format(new Date()));
        processed = processed.replace("{uptime}", getUptime());
        
        processed = processed.replace("{server_name}", Bukkit.getServer().getName());
        processed = processed.replace("{server_name}", Bukkit.getServer().getName());
        processed = processed.replace("{server_version}", Bukkit.getVersion());
        processed = processed.replace("{bukkit_version}", Bukkit.getBukkitVersion());
        processed = processed.replace("{world_count}", String.valueOf(Bukkit.getWorlds().size()));
        processed = processed.replace("{plugin_count}", String.valueOf(Bukkit.getPluginManager().getPlugins().length));
        processed = processed.replace("{tps}", getTPS());
        processed = processed.replace("{memory_used}", getMemoryUsed());
        processed = processed.replace("{memory_max}", getMemoryMax());
        processed = processed.replace("{cpu_usage}", getCPUUsage());
        
        processed = processed.replace("{day_of_week}", getDayOfWeek());
        processed = processed.replace("{month}", getMonth());
        processed = processed.replace("{year}", String.valueOf(new Date().getYear() + 1900));

        for (Map.Entry<String, String> entry : customPlaceholders.entrySet()) {
            processed = processed.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        return ChatColor.translateAlternateColorCodes('&', processed);
    }
    
    public void registerCustomPlaceholder(String placeholder, String value) {
        customPlaceholders.put(placeholder, value);
    }
    
    public void removeCustomPlaceholder(String placeholder) {
        customPlaceholders.remove(placeholder);
    }
    
    public Map<String, String> getCustomPlaceholders() {
        return new HashMap<>(customPlaceholders);
    }
    
    
    private String getEventName() {
        if (plugin.getConfigManager().isCustomEventActive()) {
            return plugin.getConfigManager().getCustomEventName();
        } else if (plugin.getEventManager().hasActiveEvent()) {
            return plugin.getEventManager().getActiveEventName();
        }
        return "Nenhum";
    }
    
    private String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime();
        long days = TimeUnit.MILLISECONDS.toDays(uptimeMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
        
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        } else {
            return String.format("%dh %dm", hours, minutes);
        }
    }
    
    private String getTPS() {
        try {
            Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            double[] tps = (double[]) server.getClass().getField("recentTps").get(server);
            return String.format("%.1f", Math.min(tps[0], 20.0));
        } catch (Exception e) {
            return "20.0";
        }
    }
    
    private String getMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        return used + "MB";
    }
    
    private String getMemoryMax() {
        Runtime runtime = Runtime.getRuntime();
        long max = runtime.maxMemory() / 1024 / 1024;
        return max + "MB";
    }
    
    private String getCPUUsage() {
        try {
            com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuUsage = osBean.getProcessCpuLoad() * 100;
            return String.format("%.1f%%", cpuUsage);
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    private String getDayOfWeek() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        return dayFormat.format(new Date());
    }
    
    private String getMonth() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        return monthFormat.format(new Date());
    }
}