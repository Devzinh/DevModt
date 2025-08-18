package me.devplugins.devModt.managers;

import me.devplugins.devModt.DevModt;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventManager {
    
    private final DevModt plugin;
    private final ConfigManager configManager;
    
    public EventManager(DevModt plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    public boolean hasActiveEvent() {
        return getActiveEventName() != null;
    }
    
    public String getActiveEventName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        String currentDate = dateFormat.format(new Date());
        
        String[] events = {"natal", "ano_novo", "halloween"};
        
        for (String eventName : events) {
            String startDate = configManager.getEventStartDate(eventName);
            String endDate = configManager.getEventEndDate(eventName);
            
            if (isDateInRange(currentDate, startDate, endDate)) {
                return eventName;
            }
        }
        
        return null;
    }
    
    public String getActiveEventMessage() {
        String eventName = getActiveEventName();
        if (eventName != null) {
            return configManager.getEventMessage(eventName);
        }
        return "";
    }
    
    private boolean isDateInRange(String currentDate, String startDate, String endDate) {
        if (startDate == null || endDate == null || currentDate == null) {
            return false;
        }
        
        try {
            String[] currentParts = currentDate.split("-");
            String[] startParts = startDate.split("-");
            String[] endParts = endDate.split("-");
            
            int currentMonth = Integer.parseInt(currentParts[0]);
            int currentDay = Integer.parseInt(currentParts[1]);
            int startMonth = Integer.parseInt(startParts[0]);
            int startDay = Integer.parseInt(startParts[1]);
            int endMonth = Integer.parseInt(endParts[0]);
            int endDay = Integer.parseInt(endParts[1]);
            
            if (startMonth == endMonth) {
                return currentMonth == startMonth && currentDay >= startDay && currentDay <= endDay;
            } else if (startMonth > endMonth) {
                return (currentMonth == startMonth && currentDay >= startDay) || 
                       (currentMonth == endMonth && currentDay <= endDay) ||
                       (currentMonth > startMonth || currentMonth < endMonth);
            } else {
                return (currentMonth == startMonth && currentDay >= startDay) || 
                       (currentMonth == endMonth && currentDay <= endDay) ||
                       (currentMonth > startMonth && currentMonth < endMonth);
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Erro ao processar datas do evento: " + e.getMessage());
            return false;
        }
    }
    
    public void checkAndLogEvents() {
        if (hasActiveEvent()) {
            String eventName = getActiveEventName();
            if (configManager.isLoggingEnabled()) {
                plugin.getLogger().info("Evento ativo detectado: " + eventName);
            }
        }
    }
}