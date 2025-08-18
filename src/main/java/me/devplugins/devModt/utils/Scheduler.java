package me.devplugins.devModt.utils;

import me.devplugins.devModt.DevModt;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Scheduler {
    
    private final DevModt plugin;
    private BukkitTask eventCheckTask;
    
    public Scheduler(DevModt plugin) {
        this.plugin = plugin;
    }
    
    public void startScheduler() {
        startEventCheckScheduler();
        
        plugin.getMotdManager().updateMOTD();
        
        if (plugin.getConfigManager().isLoggingEnabled()) {
            plugin.getLogger().info("Scheduler iniciado - Verificação de eventos a cada 30 minutos");
        }
    }
    
    private void startEventCheckScheduler() {
        eventCheckTask = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getEventManager().checkAndLogEvents();
                plugin.getMotdManager().updateMOTD("EVENT");
            }
        }.runTaskTimer(plugin, 20L * 60L, 20L * 60L * 30L);
    }
    
    public void scheduleDelayedUpdate() {
        scheduleDelayedUpdate("DELAYED");
    }
    
    public void scheduleDelayedUpdate(String triggerType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getMotdManager().updateMOTD(triggerType);
            }
        }.runTaskLater(plugin, 20L);
    }
    
    public void stopScheduler() {
        if (eventCheckTask != null && !eventCheckTask.isCancelled()) {
            eventCheckTask.cancel();
        }
        
        if (plugin.getConfigManager().isLoggingEnabled()) {
            plugin.getLogger().info("Scheduler parado");
        }
    }
    
    public void restartScheduler() {
        stopScheduler();
        startScheduler();
        
        if (plugin.getConfigManager().isLoggingEnabled()) {
            plugin.getLogger().info("Scheduler reiniciado");
        }
    }
}