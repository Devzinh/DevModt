package me.devplugins.devModt.managers;

import me.devplugins.devModt.DevModt;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    
    private final DevModt plugin;
    private Connection connection;
    private final String databasePath;
    
    public DatabaseManager(DevModt plugin) {
        this.plugin = plugin;
        this.databasePath = plugin.getDataFolder().getAbsolutePath() + File.separator + "devmodt.db";
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            
            createTables();
            
            plugin.getLogger().info("Banco de dados inicializado com sucesso!");
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao inicializar banco de dados: " + e.getMessage());
        }
    }
    
    private void createTables() throws SQLException {
        String motdHistoryTable = """
            CREATE TABLE IF NOT EXISTS motd_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                motd TEXT NOT NULL,
                timestamp BIGINT NOT NULL,
                trigger_type TEXT NOT NULL,
                player_count INTEGER NOT NULL
            )
        """;
        
        String statisticsTable = """
            CREATE TABLE IF NOT EXISTS statistics (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                total_updates INTEGER DEFAULT 0,
                weather_updates INTEGER DEFAULT 0,
                event_updates INTEGER DEFAULT 0,
                manual_updates INTEGER DEFAULT 0,
                avg_players REAL DEFAULT 0.0
            )
        """;
        
        String customEventsTable = """
            CREATE TABLE IF NOT EXISTS custom_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                message TEXT NOT NULL,
                created_by TEXT NOT NULL,
                created_at BIGINT NOT NULL,
                active BOOLEAN DEFAULT FALSE
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(motdHistoryTable);
            stmt.execute(statisticsTable);
            stmt.execute(customEventsTable);
        }
    }
    
    public void logMOTDUpdate(String motd, String triggerType, int playerCount) {
        String sql = "INSERT INTO motd_history (motd, timestamp, trigger_type, player_count) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, motd);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, triggerType);
            pstmt.setInt(4, playerCount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao registrar atualização do MOTD: " + e.getMessage());
        }
    }
    
    public void updateDailyStatistics() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        
        String selectSql = "SELECT COUNT(*) as total FROM motd_history WHERE date(timestamp/1000, 'unixepoch') = ?";
        String insertSql = """
            INSERT OR REPLACE INTO statistics (date, total_updates, weather_updates, event_updates, manual_updates, avg_players)
            VALUES (?, 
                (SELECT COUNT(*) FROM motd_history WHERE date(timestamp/1000, 'unixepoch') = ?),
                (SELECT COUNT(*) FROM motd_history WHERE date(timestamp/1000, 'unixepoch') = ? AND trigger_type = 'WEATHER'),
                (SELECT COUNT(*) FROM motd_history WHERE date(timestamp/1000, 'unixepoch') = ? AND trigger_type = 'EVENT'),
                (SELECT COUNT(*) FROM motd_history WHERE date(timestamp/1000, 'unixepoch') = ? AND trigger_type = 'MANUAL'),
                (SELECT AVG(player_count) FROM motd_history WHERE date(timestamp/1000, 'unixepoch') = ?)
            )
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            pstmt.setString(1, today);
            pstmt.setString(2, today);
            pstmt.setString(3, today);
            pstmt.setString(4, today);
            pstmt.setString(5, today);
            pstmt.setString(6, today);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao atualizar estatísticas diárias: " + e.getMessage());
        }
    }
    
    public List<Map<String, Object>> getMOTDHistory(int limit) {
        List<Map<String, Object>> history = new ArrayList<>();
        String sql = "SELECT * FROM motd_history ORDER BY timestamp DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", rs.getInt("id"));
                record.put("motd", rs.getString("motd"));
                record.put("timestamp", rs.getLong("timestamp"));
                record.put("trigger_type", rs.getString("trigger_type"));
                record.put("player_count", rs.getInt("player_count"));
                history.add(record);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao buscar histórico do MOTD: " + e.getMessage());
        }
        
        return history;
    }
    
    public Map<String, Object> getTodayStatistics() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String sql = "SELECT * FROM statistics WHERE date = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, today);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("total_updates", rs.getInt("total_updates"));
                stats.put("weather_updates", rs.getInt("weather_updates"));
                stats.put("event_updates", rs.getInt("event_updates"));
                stats.put("manual_updates", rs.getInt("manual_updates"));
                stats.put("avg_players", rs.getDouble("avg_players"));
                return stats;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao buscar estatísticas de hoje: " + e.getMessage());
        }
        
        return new HashMap<>();
    }
    
    public void saveCustomEvent(String name, String message, String createdBy) {
        String sql = "INSERT INTO custom_events (name, message, created_by, created_at, active) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, message);
            pstmt.setString(3, createdBy);
            pstmt.setLong(4, System.currentTimeMillis());
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao salvar evento customizado: " + e.getMessage());
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Conexão com banco de dados fechada.");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Erro ao fechar conexão com banco de dados: " + e.getMessage());
        }
    }
}