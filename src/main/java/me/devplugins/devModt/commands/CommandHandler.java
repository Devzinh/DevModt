package me.devplugins.devModt.commands;

import me.devplugins.devModt.DevModt;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {
    
    private final DevModt plugin;
    
    public CommandHandler(DevModt plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("devmotd.admin")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                handleReload(sender);
                break;
            case "setevent":
                handleSetEvent(sender, args);
                break;
            case "clearevent":
                handleClearEvent(sender);
                break;
            case "preview":
                handlePreview(sender);
                break;
            case "stats":
            case "statistics":
                handleStatistics(sender);
                break;
            case "metrics":
                handleMetrics(sender);
                break;
            case "history":
                handleHistory(sender, args);
                break;
            case "placeholder":
                handlePlaceholder(sender, args);
                break;
            case "test":
                handleTest(sender, args);
                break;
            case "update":
                handleUpdate(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            case "performance":
                handlePerformance(sender);
                break;
            case "force":
                handleForceUpdate(sender);
                break;
            case "debug":
                handleDebug(sender);
                break;
            case "events":
            case "listevent":
                handleListEvents(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().reloadConfig();
            plugin.getMotdManager().updateMOTD();
            sender.sendMessage(ChatColor.GREEN + "DevModt recarregado com sucesso!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Erro ao recarregar: " + e.getMessage());
            plugin.getLogger().severe("Erro ao recarregar configuração: " + e.getMessage());
        }
    }
    
    private void handleSetEvent(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /devmotd setevent <nome> [mensagem]");
            return;
        }
        
        String eventName = args[1].toLowerCase();
        String eventMessage;
        
        String preConfiguredMessage = plugin.getConfigManager().getEventMessage(eventName);
        
        if (preConfiguredMessage != null && !preConfiguredMessage.isEmpty()) {
            eventMessage = preConfiguredMessage;
            String displayName = plugin.getConfigManager().getString("events." + eventName + ".name");
            if (displayName.isEmpty()) {
                displayName = eventName;
            }
            
            plugin.getConfigManager().setCustomEvent(displayName, eventMessage);
            plugin.getDatabaseManager().saveCustomEvent(displayName, eventMessage, sender.getName());
            plugin.getMotdManager().updateMOTD("MANUAL");
            
            sender.sendMessage(ChatColor.GREEN + "Evento pré-configurado ativado: " + ChatColor.YELLOW + displayName);
            sender.sendMessage(ChatColor.GRAY + "Mensagem: " + ChatColor.translateAlternateColorCodes('&', eventMessage.replace("\n", " | ")));
            return;
        }
        
        if (args.length > 2) {
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                messageBuilder.append(args[i]);
                if (i < args.length - 1) {
                    messageBuilder.append(" ");
                }
            }
            eventMessage = messageBuilder.toString();
        } else {
            eventMessage = "&d&l🎉 &5&l" + eventName.toUpperCase() + " &d&l🎉\n&7Evento especial ativo! &8| &7Evento: &d" + eventName;
        }
        
        plugin.getConfigManager().setCustomEvent(eventName, eventMessage);
        plugin.getDatabaseManager().saveCustomEvent(eventName, eventMessage, sender.getName());
        plugin.getMotdManager().updateMOTD("MANUAL");
        
        sender.sendMessage(ChatColor.GREEN + "Evento customizado definido: " + ChatColor.YELLOW + eventName);
        sender.sendMessage(ChatColor.GRAY + "Mensagem: " + ChatColor.translateAlternateColorCodes('&', eventMessage.replace("\n", " | ")));
    }
    
    private void handleClearEvent(CommandSender sender) {
        plugin.getConfigManager().clearCustomEvent();
        plugin.getMotdManager().updateMOTD("MANUAL");
        sender.sendMessage(ChatColor.GREEN + "Evento customizado removido!");
    }
    
    private void handlePreview(CommandSender sender) {
        String preview = plugin.getMotdManager().getPreviewMOTD();
        sender.sendMessage(ChatColor.YELLOW + "Preview do MOTD atual:");
        sender.sendMessage(ChatColor.GRAY + "─────────────────────────");
        
        String[] lines = preview.split("\n");
        for (String line : lines) {
            sender.sendMessage(line);
        }
        
        sender.sendMessage(ChatColor.GRAY + "─────────────────────────");
        sender.sendMessage(ChatColor.GRAY + "Sem cores: " + ChatColor.stripColor(preview.replace("\n", " | ")));
    }
    
    private void handleStatistics(CommandSender sender) {
        var stats = plugin.getDatabaseManager().getTodayStatistics();
        
        sender.sendMessage(ChatColor.GOLD + "=== DevModt - Estatísticas de Hoje ===");
        sender.sendMessage(ChatColor.YELLOW + "Total de Atualizações: " + ChatColor.WHITE + stats.getOrDefault("total_updates", 0));
        sender.sendMessage(ChatColor.YELLOW + "Por Clima: " + ChatColor.WHITE + stats.getOrDefault("weather_updates", 0));
        sender.sendMessage(ChatColor.YELLOW + "Por Evento: " + ChatColor.WHITE + stats.getOrDefault("event_updates", 0));
        sender.sendMessage(ChatColor.YELLOW + "Manuais: " + ChatColor.WHITE + stats.getOrDefault("manual_updates", 0));
        sender.sendMessage(ChatColor.YELLOW + "Média de Jogadores: " + ChatColor.WHITE + String.format("%.1f", stats.getOrDefault("avg_players", 0.0)));
        sender.sendMessage(ChatColor.GOLD + "=====================================");
    }
    
    private void handleMetrics(CommandSender sender) {
        String report = plugin.getMetricsManager().getPerformanceReport();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('§', report));
    }
    
    private void handleHistory(CommandSender sender, String[] args) {
        int limit = 10;
        if (args.length > 1) {
            try {
                limit = Integer.parseInt(args[1]);
                limit = Math.min(limit, 50); // Máximo 50
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Número inválido! Usando padrão: 10");
            }
        }
        
        var history = plugin.getDatabaseManager().getMOTDHistory(limit);
        
        sender.sendMessage(ChatColor.GOLD + "=== DevModt - Histórico (últimos " + limit + ") ===");
        
        if (history.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Nenhum registro encontrado.");
            return;
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM HH:mm");
        
        for (var record : history) {
            long timestamp = (Long) record.get("timestamp");
            String motd = (String) record.get("motd");
            String trigger = (String) record.get("trigger_type");
            int players = (Integer) record.get("player_count");
            
            String date = sdf.format(new java.util.Date(timestamp));
            String cleanMOTD = ChatColor.stripColor(motd.replace("\n", " | "));
            
            sender.sendMessage(ChatColor.GRAY + "[" + date + "] " + 
                             ChatColor.YELLOW + trigger + ChatColor.GRAY + " (" + players + " players): " + 
                             ChatColor.WHITE + (cleanMOTD.length() > 50 ? cleanMOTD.substring(0, 50) + "..." : cleanMOTD));
        }
        
        sender.sendMessage(ChatColor.GOLD + "=======================================");
    }
    
    private void handlePlaceholder(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /devmotd placeholder <list|add|remove> [nome] [valor]");
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "list":
                var placeholders = plugin.getPlaceholderManager().getCustomPlaceholders();
                sender.sendMessage(ChatColor.GOLD + "=== Placeholders Customizados ===");
                if (placeholders.isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "Nenhum placeholder customizado.");
                } else {
                    placeholders.forEach((key, value) -> 
                        sender.sendMessage(ChatColor.YELLOW + "{" + key + "}" + ChatColor.GRAY + " = " + ChatColor.WHITE + value));
                }
                break;
                
            case "add":
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "Uso: /devmotd placeholder add <nome> <valor>");
                    return;
                }
                String name = args[2];
                StringBuilder valueBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    valueBuilder.append(args[i]);
                    if (i < args.length - 1) valueBuilder.append(" ");
                }
                String value = valueBuilder.toString();
                
                plugin.getPlaceholderManager().registerCustomPlaceholder(name, value);
                sender.sendMessage(ChatColor.GREEN + "Placeholder {" + name + "} adicionado com valor: " + value);
                break;
                
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /devmotd placeholder remove <nome>");
                    return;
                }
                plugin.getPlaceholderManager().removeCustomPlaceholder(args[2]);
                sender.sendMessage(ChatColor.GREEN + "Placeholder {" + args[2] + "} removido!");
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "Uso: /devmotd placeholder <list|add|remove> [nome] [valor]");
        }
    }
    
    private void handleTest(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /devmotd test <mensagem>");
            return;
        }
        
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) messageBuilder.append(" ");
        }
        
        String testMessage = messageBuilder.toString();
        String processed = plugin.getPlaceholderManager().processPlaceholders(testMessage);
        
        sender.sendMessage(ChatColor.YELLOW + "Teste de Mensagem:");
        sender.sendMessage(ChatColor.GRAY + "Original: " + ChatColor.WHITE + testMessage);
        sender.sendMessage(ChatColor.GRAY + "Processada: " + processed);
        sender.sendMessage(ChatColor.GRAY + "Sem cores: " + ChatColor.stripColor(processed));
    }
    
    private void handleUpdate(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Verificando atualizações...");
        plugin.getUpdateChecker().checkForUpdates();
    }
    
    private void handleInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== DevModt - Informações ===");
        sender.sendMessage(ChatColor.YELLOW + "Versão: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Desenvolvedor: " + ChatColor.WHITE + "DevPlugins Team");
        sender.sendMessage(ChatColor.YELLOW + "Discord: " + ChatColor.WHITE + "discord.gg/A4F9jtGhFU");
        sender.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GREEN + "Ativo");
        sender.sendMessage(ChatColor.YELLOW + "Uptime: " + ChatColor.WHITE + getPluginUptime());
        sender.sendMessage(ChatColor.YELLOW + "MOTD Atual: " + ChatColor.WHITE + ChatColor.stripColor(plugin.getMotdManager().getCurrentMOTD().replace("\n", " | ")));
        sender.sendMessage(ChatColor.GOLD + "============================");
    }
    
    private String getPluginUptime() {
        long uptime = System.currentTimeMillis() - java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(uptime);
        long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(uptime) % 60;
        return String.format("%dh %dm", hours, minutes);
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== DevModt v2.0 - Comandos Profissionais ===");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd reload" + ChatColor.GRAY + " - Recarrega as configurações");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd setevent <nome> [msg]" + ChatColor.GRAY + " - Ativa evento (pré-config ou custom)");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd clearevent" + ChatColor.GRAY + " - Remove evento customizado");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd preview" + ChatColor.GRAY + " - Preview do MOTD atual");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd stats" + ChatColor.GRAY + " - Estatísticas de hoje");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd metrics" + ChatColor.GRAY + " - Métricas de performance");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd history [limite]" + ChatColor.GRAY + " - Histórico de atualizações");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd placeholder <list|add|remove>" + ChatColor.GRAY + " - Gerenciar placeholders");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd test <mensagem>" + ChatColor.GRAY + " - Testar processamento");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd update" + ChatColor.GRAY + " - Verificar atualizações");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd info" + ChatColor.GRAY + " - Informações do plugin");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd performance" + ChatColor.GRAY + " - Métricas de performance");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd force" + ChatColor.GRAY + " - Força atualização do MOTD");
        sender.sendMessage(ChatColor.YELLOW + "/devmotd events" + ChatColor.GRAY + " - Lista eventos pré-configurados");
        sender.sendMessage(ChatColor.GOLD + "============================================");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("devmotd.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            List<String> completions = Arrays.asList("reload", "setevent", "clearevent", "preview", 
                "stats", "metrics", "history", "placeholder", "test", "update", "info", "performance", "force", "events", "debug");
            List<String> result = new ArrayList<>();
            
            for (String completion : completions) {
                if (completion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(completion);
                }
            }
            
            return result;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("placeholder")) {
                return Arrays.asList("list", "add", "remove");
            } else if (args[0].equalsIgnoreCase("history")) {
                return Arrays.asList("10", "25", "50");
            } else if (args[0].equalsIgnoreCase("setevent")) {
                List<String> events = Arrays.asList("gladiador", "pvp", "build", "minigames", "economia", "natal", "halloween", "ano_novo");
                List<String> result = new ArrayList<>();
                
                for (String event : events) {
                    if (event.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(event);
                    }
                }
                
                return result;
            }
        }
        
        return new ArrayList<>();
    }
    
    private void handlePerformance(CommandSender sender) {
        plugin.getMotdManager().printPerformanceStats();
        
        if (plugin.getMetricsManager() != null) {
            var metrics = plugin.getMetricsManager().getPerformanceMetrics();
            sender.sendMessage(ChatColor.GOLD + "=== Performance ===");
            sender.sendMessage(ChatColor.YELLOW + "Tempo médio de processamento: " + ChatColor.WHITE + 
                String.format("%.2fms", (Double) metrics.getOrDefault("avg_processing_time", 0.0)));
            sender.sendMessage(ChatColor.YELLOW + "Atualizações na última hora: " + ChatColor.WHITE + 
                metrics.getOrDefault("updates_last_hour", 0));
        }
        
        sender.sendMessage(ChatColor.GREEN + "Estatísticas de performance enviadas para o console!");
    }
    
    private void handleForceUpdate(CommandSender sender) {
        plugin.getMotdManager().forceUpdateMOTD("MANUAL_FORCE");
        sender.sendMessage(ChatColor.GREEN + "MOTD atualizado forçadamente!");
    }
    
    private void handleDebug(CommandSender sender) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        
        String timeRange;
        if (hour >= 6 && hour < 12) {
            timeRange = "morning";
        } else if (hour >= 12 && hour < 18) {
            timeRange = "afternoon";
        } else if (hour >= 18 && hour < 23) {
            timeRange = "evening";
        } else {
            timeRange = "night";
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== DEBUG MOTD ===");
        sender.sendMessage(ChatColor.YELLOW + "Hora atual: " + ChatColor.WHITE + hour);
        sender.sendMessage(ChatColor.YELLOW + "Período: " + ChatColor.WHITE + timeRange);
        sender.sendMessage(ChatColor.YELLOW + "Time-based enabled: " + ChatColor.WHITE + plugin.getConfigManager().isTimeBasedEnabled());
        sender.sendMessage(ChatColor.YELLOW + "Events enabled: " + ChatColor.WHITE + plugin.getConfigManager().isEventsEnabled());
        sender.sendMessage(ChatColor.YELLOW + "Custom event active: " + ChatColor.WHITE + plugin.getConfigManager().isCustomEventActive());
        
        String timeMessage = plugin.getConfigManager().getTimeMessage(timeRange);
        sender.sendMessage(ChatColor.YELLOW + "Mensagem do período: " + ChatColor.WHITE + (timeMessage.isEmpty() ? "VAZIA!" : timeMessage));
        
        sender.sendMessage(ChatColor.YELLOW + "MOTD atual: " + ChatColor.WHITE + plugin.getMotdManager().getCurrentMOTD());
    }
    
    private void handleListEvents(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Eventos Pré-Configurados ===");
        sender.sendMessage(ChatColor.YELLOW + "gladiador" + ChatColor.GRAY + " - Evento de combate PvP");
        sender.sendMessage(ChatColor.YELLOW + "pvp" + ChatColor.GRAY + " - Evento de batalhas");
        sender.sendMessage(ChatColor.YELLOW + "build" + ChatColor.GRAY + " - Evento de construção");
        sender.sendMessage(ChatColor.YELLOW + "minigames" + ChatColor.GRAY + " - Evento de mini-jogos");
        sender.sendMessage(ChatColor.YELLOW + "economia" + ChatColor.GRAY + " - Evento econômico");
        sender.sendMessage(ChatColor.YELLOW + "natal" + ChatColor.GRAY + " - Evento de Natal");
        sender.sendMessage(ChatColor.YELLOW + "halloween" + ChatColor.GRAY + " - Evento de Halloween");
        sender.sendMessage(ChatColor.YELLOW + "ano_novo" + ChatColor.GRAY + " - Evento de Ano Novo");
        sender.sendMessage(ChatColor.GOLD + "===============================");
        sender.sendMessage(ChatColor.GRAY + "Use: " + ChatColor.WHITE + "/devmotd setevent <nome>" + ChatColor.GRAY + " para ativar");
    }
}