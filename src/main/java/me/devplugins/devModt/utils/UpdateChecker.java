package me.devplugins.devModt.utils;

import me.devplugins.devModt.DevModt;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    
    private final DevModt plugin;
    private final String currentVersion;
    private final String updateURL = "https://api.github.com/repos/Devzinh/Plugins-Minecraft/releases/latest";
    
    public UpdateChecker(DevModt plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }
    
    public void checkForUpdates() {
        CompletableFuture.runAsync(() -> {
            try {
                String latestVersion = getLatestVersion();
                if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                    notifyUpdate(latestVersion);
                } else if (plugin.getConfigManager().isLoggingEnabled()) {
                    plugin.getLogger().info("DevModt está atualizado! Versão: " + currentVersion);
                }
            } catch (Exception e) {
                if (plugin.getConfigManager().isLoggingEnabled()) {
                    plugin.getLogger().warning("Não foi possível verificar atualizações: " + e.getMessage());
                }
            }
        });
    }
    
    private String getLatestVersion() {
        try {
            URL url = new URL(updateURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "DevModt-UpdateChecker");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                

                String jsonResponse = response.toString();
                int tagStart = jsonResponse.indexOf("\"tag_name\":\"") + 12;
                int tagEnd = jsonResponse.indexOf("\"", tagStart);
                
                if (tagStart > 11 && tagEnd > tagStart) {
                    return jsonResponse.substring(tagStart, tagEnd);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao verificar atualizações: " + e.getMessage());
        }
        
        return null;
    }
    
    private void notifyUpdate(String latestVersion) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.getLogger().info("==========================================");
            plugin.getLogger().info("        NOVA VERSÃO DISPONÍVEL");
            plugin.getLogger().info("");
            plugin.getLogger().info("  Versão Atual: " + currentVersion);
            plugin.getLogger().info("  Nova Versão:  " + latestVersion);
            plugin.getLogger().info("");
            plugin.getLogger().info("  Baixe em: github.com/devplugins");
            plugin.getLogger().info("==========================================");
            
            Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("devmotd.admin"))
                .forEach(player -> {
                    player.sendMessage("§6[DevModt] §eNova versão disponível: §a" + latestVersion);
                    player.sendMessage("§6[DevModt] §eVersão atual: §c" + currentVersion);
                });
        });
    }
}