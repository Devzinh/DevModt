package me.devplugins.devModt.managers;

import me.devplugins.devModt.DevModt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReference;

public class MOTDManager {
    
    private final DevModt plugin;
    private final ConfigManager configManager;
    private final EventManager eventManager;
    private final AtomicReference<String> currentMOTD = new AtomicReference<>("");
    
    private final ConcurrentHashMap<String, String> motdCache = new ConcurrentHashMap<>();
    private long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 30000;
    private static final int PRIORITY_CUSTOM_EVENT = 100;
    private static final int PRIORITY_SPECIAL_EVENT = 90;
    private static final int PRIORITY_TIME = 70;
    
    public MOTDManager(DevModt plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.eventManager = plugin.getEventManager();
    }
    
    public void updateMOTD() {
        updateMOTD("ROTATION");
    }
    
    public void updateMOTD(String triggerType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateMOTDAsync(triggerType);
            }
        }.runTaskAsynchronously(plugin);
    }
    
    private void updateMOTDAsync(String triggerType) {
        long startTime = System.currentTimeMillis();
        String oldMOTD = currentMOTD.get();
        String newMOTD = generateMOTDWithPriority();
        
        if (!newMOTD.equals(oldMOTD)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    currentMOTD.set(newMOTD);
                    Bukkit.getServer().setMotd(newMOTD);
                    
                    long processingTime = System.currentTimeMillis() - startTime;
                    
                    if (plugin.getMetricsManager() != null) {
                        plugin.getMetricsManager().recordMOTDUpdate(triggerType, processingTime);
                    }
                    
                    if (plugin.getDatabaseManager() != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                plugin.getDatabaseManager().logMOTDUpdate(newMOTD, triggerType, Bukkit.getOnlinePlayers().size());
                            }
                        }.runTaskAsynchronously(plugin);
                    }
                    
                    if (plugin.getServer().getPluginManager().isPluginEnabled("DevModt")) {
                        me.devplugins.devModt.api.DevModtAPI.fireMOTDUpdateEvent(oldMOTD, newMOTD);
                    }
                    
                    if (configManager.isLoggingEnabled()) {
                        plugin.getLogger().info("MOTD atualizado (" + triggerType + ", " + processingTime + "ms): " + 
                            ChatColor.stripColor(newMOTD.replace("\n", " | ")));
                    }
                }
            }.runTask(plugin);
        }
    }
    
    public void forceUpdateMOTD(String triggerType) {
        clearCache();
        updateMOTD(triggerType);
    }
    
    private void clearCache() {
        motdCache.clear();
        lastCacheUpdate = 0;
    }
    
    private String generateMOTDWithPriority() {
        // Verificar cache primeiro
        String cacheKey = generateCacheKey();
        if (System.currentTimeMillis() - lastCacheUpdate < CACHE_DURATION && motdCache.containsKey(cacheKey)) {
            return motdCache.get(cacheKey);
        }

        MOTDCandidate bestCandidate = new MOTDCandidate("", 0);

        // Prioridade 1: Evento customizado
        if (configManager.isCustomEventActive()) {
            bestCandidate = new MOTDCandidate(configManager.getCustomEventMessage(), PRIORITY_CUSTOM_EVENT);
        }

        // Prioridade 2: Eventos especiais
        if (configManager.isEventsEnabled() && eventManager.hasActiveEvent()) {
            MOTDCandidate candidate = new MOTDCandidate(eventManager.getActiveEventMessage(), PRIORITY_SPECIAL_EVENT);
            if (candidate.priority > bestCandidate.priority) {
                bestCandidate = candidate;
            }
        }

        // Prioridade 3: Mensagens por horário
        if (configManager.isTimeBasedEnabled()) {
            String timeMOTD = getTimeBasedMOTD();
            if (!timeMOTD.isEmpty()) {
                MOTDCandidate candidate = new MOTDCandidate(timeMOTD, PRIORITY_TIME);
                if (candidate.priority > bestCandidate.priority) {
                    bestCandidate = candidate;
                }
            }
        }

        // Se nenhuma mensagem foi definida, usar mensagem padrão
        if (bestCandidate.message.isEmpty()) {
            bestCandidate = new MOTDCandidate("&6&lServidor DevTestes &8| &7Entre e divirta-se!", PRIORITY_TIME - 1);
        }

        String finalMOTD = plugin.getPlaceholderManager().processPlaceholders(bestCandidate.message);

        motdCache.put(cacheKey, finalMOTD);
        lastCacheUpdate = System.currentTimeMillis();

        return finalMOTD;
    }

    private String generateCacheKey() {
        StringBuilder key = new StringBuilder();
        key.append(Bukkit.getOnlinePlayers().size()).append("_");
        key.append(configManager.isCustomEventActive()).append("_");
        key.append(eventManager.hasActiveEvent()).append("_");

        long time = System.currentTimeMillis() / 60000;
        key.append(time);

        return key.toString();
    }

    private static class MOTDCandidate {
        final String message;
        final int priority;

        MOTDCandidate(String message, int priority) {
            this.message = message;
            this.priority = priority;
        }
    }

    private String getTimeBasedMOTD() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 12) {
            return configManager.getTimeMessage("morning");
        } else if (hour >= 12 && hour < 18) {
            return configManager.getTimeMessage("afternoon");
        } else if (hour >= 18 && hour < 23) {
            return configManager.getTimeMessage("evening");
        } else {
            return configManager.getTimeMessage("night");
        }
    }



    public String getCurrentMOTD() {
        return currentMOTD.get();
    }

    public String getPreviewMOTD() {
        return plugin.getPlaceholderManager().processPlaceholders(generateMOTDWithPriority());
    }

    // Método para outros plugins verificarem se podem alterar o MOTD
    public boolean canOverrideMOTD(int priority) {
        return priority > PRIORITY_CUSTOM_EVENT;
    }

    // Método para outros plugins definirem MOTD temporário
    public void setTemporaryMOTD(String motd, int durationSeconds, String source) {
        if (motd == null || motd.isEmpty()) return;

        String processedMOTD = plugin.getPlaceholderManager().processPlaceholders(motd);
        currentMOTD.set(processedMOTD);
        Bukkit.getServer().setMotd(processedMOTD);

        if (configManager.isLoggingEnabled()) {
            plugin.getLogger().info("MOTD temporário definido por " + source + " por " + durationSeconds + "s");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                forceUpdateMOTD("TEMPORARY_EXPIRED");
            }
        }.runTaskLater(plugin, durationSeconds * 20L);
    }

    public void printPerformanceStats() {
        if (configManager.isLoggingEnabled()) {
            plugin.getLogger().info("Cache MOTD: " + motdCache.size() + " entradas, última atualização: " +
                (System.currentTimeMillis() - lastCacheUpdate) + "ms atrás");
        }
    }
}