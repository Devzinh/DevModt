package me.devplugins.devModt.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Evento disparado quando o MOTD é atualizado
 */
public class MOTDUpdateEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private final String oldMOTD;
    private final String newMOTD;
    private final long timestamp;
    
    public MOTDUpdateEvent(String oldMOTD, String newMOTD) {
        this.oldMOTD = oldMOTD;
        this.newMOTD = newMOTD;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getOldMOTD() {
        return oldMOTD;
    }
    
    public String getNewMOTD() {
        return newMOTD;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}