package me.devplugins.devModt.api;

import me.devplugins.devModt.DevModt;
import me.devplugins.devModt.api.events.MOTDUpdateEvent;
import org.bukkit.Bukkit;

/**
 * DevModt Public API
 * Permite que outros plugins interajam com o DevModt
 */
public class DevModtAPI {
    
    private static DevModt plugin;
    
    public static void initialize(DevModt pluginInstance) {
        plugin = pluginInstance;
    }
    
    /**
     * Força uma atualização imediata do MOTD
     */
    public static void forceUpdateMOTD() {
        if (plugin != null) {
            plugin.getMotdManager().updateMOTD();
        }
    }
    
    /**
     * Define um evento customizado via API
     * @param eventName
     * @param message
     */
    public static void setCustomEvent(String eventName, String message) {
        if (plugin != null) {
            plugin.getConfigManager().setCustomEvent(eventName, message);
            plugin.getMotdManager().updateMOTD();
        }
    }
    
    /**
     * Remove o evento customizado atual
     */
    public static void clearCustomEvent() {
        if (plugin != null) {
            plugin.getConfigManager().clearCustomEvent();
            plugin.getMotdManager().updateMOTD();
        }
    }
    
    /**
     * Obtém o MOTD atual processado
     * @return MOTD atual com cores e placeholders processados
     */
    public static String getCurrentMOTD() {
        if (plugin != null) {
            return plugin.getMotdManager().getCurrentMOTD();
        }
        return "";
    }
    
    /**
     * Obtém preview do MOTD sem aplicá-lo
     * @return Preview do MOTD
     */
    public static String getPreviewMOTD() {
        if (plugin != null) {
            return plugin.getMotdManager().getPreviewMOTD();
        }
        return "";
    }
    
    /**
     * Registra um placeholder customizado
     * @param placeholder
     * @param value
     */
    public static void registerPlaceholder(String placeholder, String value) {
        if (plugin != null) {
            plugin.getPlaceholderManager().registerCustomPlaceholder(placeholder, value);
        }
    }
    
    /**
     * Dispara evento de atualização do MOTD
     * @param oldMOTD
     * @param newMOTD
     */
    public static void fireMOTDUpdateEvent(String oldMOTD, String newMOTD) {
        MOTDUpdateEvent event = new MOTDUpdateEvent(oldMOTD, newMOTD);
        Bukkit.getPluginManager().callEvent(event);
    }
}