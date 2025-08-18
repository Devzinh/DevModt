package me.devplugins.devModt.managers;

import me.devplugins.devModt.DevModt;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsManager {
    
    private final DevModt plugin;
    private final AtomicInteger totalUpdates;
    private final AtomicInteger eventUpdates;
    private final AtomicInteger manualUpdates;
    private final AtomicLong totalProcessingTime;
    private final AtomicInteger apiCalls;
    
    private long startTime;
    
    public MetricsManager(DevModt plugin) {
        this.plugin = plugin;
        this.totalUpdates = new AtomicInteger(0);
        this.eventUpdates = new AtomicInteger(0);
        this.manualUpdates = new AtomicInteger(0);
        this.totalProcessingTime = new AtomicLong(0);
        this.apiCalls = new AtomicInteger(0);
        this.startTime = System.currentTimeMillis();
    }
    
    public void recordMOTDUpdate(String triggerType, long processingTime) {
        totalUpdates.incrementAndGet();
        totalProcessingTime.addAndGet(processingTime);
        
        switch (triggerType.toUpperCase()) {
            case "EVENT":
                eventUpdates.incrementAndGet();
                break;
            case "MANUAL":
                manualUpdates.incrementAndGet();
                break;
        }
        
        if (plugin.getConfigManager().isLoggingEnabled()) {
            plugin.getLogger().info(String.format("MOTD atualizado (%s) - Tempo: %dms", triggerType, processingTime));
        }
    }
    
    public void recordAPICall() {
        apiCalls.incrementAndGet();
    }
    
    public int getTotalUpdates() {
        return totalUpdates.get();
    }
    
    public int getEventUpdates() {
        return eventUpdates.get();
    }
    
    public int getManualUpdates() {
        return manualUpdates.get();
    }
    
    public int getRotationUpdates() {
        return totalUpdates.get() - eventUpdates.get() - manualUpdates.get();
    }
    
    public long getAverageProcessingTime() {
        int total = totalUpdates.get();
        return total > 0 ? totalProcessingTime.get() / total : 0;
    }
    
    public int getAPICallsCount() {
        return apiCalls.get();
    }
    
    public double getUpdatesPerHour() {
        long uptime = System.currentTimeMillis() - startTime;
        double hours = uptime / (1000.0 * 60.0 * 60.0);
        return hours > 0 ? totalUpdates.get() / hours : 0;
    }
    
    public String getPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== DevModt - Relatório de Performance ===\n");
        report.append("Total de Atualizações: ").append(getTotalUpdates()).append("\n");
        report.append("Atualizações por Evento: ").append(getEventUpdates()).append("\n");
        report.append("Atualizações Manuais: ").append(getManualUpdates()).append("\n");
        report.append("Atualizações por Rotação: ").append(getRotationUpdates()).append("\n");
        report.append("Chamadas da API: ").append(getAPICallsCount()).append("\n");
        report.append("Tempo Médio de Processamento: ").append(getAverageProcessingTime()).append("ms\n");
        report.append("Atualizações por Hora: ").append(String.format("%.2f", getUpdatesPerHour())).append("\n");
        report.append("Jogadores Online: ").append(Bukkit.getOnlinePlayers().size()).append("/").append(Bukkit.getMaxPlayers()).append("\n");
        report.append("=====================================");
        
        return report.toString();
    }
    
    public void resetMetrics() {
        totalUpdates.set(0);
        eventUpdates.set(0);
        manualUpdates.set(0);
        totalProcessingTime.set(0);
        apiCalls.set(0);
        startTime = System.currentTimeMillis();
        
        plugin.getLogger().info("Métricas resetadas!");
    }
    
    public java.util.Map<String, Object> getPerformanceMetrics() {
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        metrics.put("avg_processing_time", (double) getAverageProcessingTime());
        metrics.put("updates_last_hour", (int) getUpdatesPerHour());
        metrics.put("total_updates", getTotalUpdates());
        metrics.put("cache_hits", 0);
        return metrics;
    }
}